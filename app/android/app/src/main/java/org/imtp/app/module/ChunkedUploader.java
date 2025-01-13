package org.imtp.app.module;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import org.imtp.app.MainApplication;
import org.imtp.app.config.OKHttpClientHelper;
import org.imtp.common.response.Result;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/26 19:53
 */
public class ChunkedUploader {

    private static final String TAG = "ChunkedUploader";

    private static final int CHUNK_SIZE = 1024 * 1024 * 5; // 5MB

    private static final int SIMPLE_UPLOAD_THRESHOLD_SIZE = 1024 * 1024 * 10; // 10MB

    private static final OKHttpClientHelper okHttpClientHelper;

    private static final ExecutorService uploadExecutor;

    static {
        okHttpClientHelper = OKHttpClientHelper.getInstance();
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                String threadPrefixName = "upload-pool-thread-";
                thread.setName(threadPrefixName + threadNumber.getAndIncrement());
                return thread;
            }
        };
        uploadExecutor = Executors.newFixedThreadPool(4,threadFactory);
    }

    public static CompletableFuture<String> uploadFile(FileInfo fileInfo) {

        return uploadFile(fileInfo,null);
    }
    public static CompletableFuture<String> uploadFile(FileInfo fileInfo,ProgressListener progressListener) {
        String filePath = fileInfo.getFilePath();
        InputStream inputStream = null;
        try {
            if (filePath.startsWith("file:")) {
                filePath = filePath.substring(6);
                File file = new File(filePath);
                inputStream = new FileInputStream(file);
            }else if (filePath.startsWith("content:")){
                Uri uri = Uri.parse(filePath);
                inputStream = MainApplication.getContext().getContentResolver().openInputStream(uri);
            }else {
                inputStream = new FileInputStream(new File(filePath));
            }
            String fileType = fileInfo.getFileType();
            return uploadFile(inputStream,fileInfo.getFilename(),fileType,CHUNK_SIZE,progressListener);
        } catch (FileNotFoundException e) {
            Log.e(TAG,"upload error: ",e);
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }


    public static CompletableFuture<String> uploadId(FileInfo fileInfo){
        int totalChunk = (int) Math.ceil((double) fileInfo.getFileSize() / CHUNK_SIZE);
        FileInfoDTO fileInfoDTO = new FileInfoDTO();
        fileInfoDTO.setFilename(fileInfo.getFilename());
        fileInfoDTO.setFileType(fileInfo.getFileType());
        fileInfoDTO.setTotalSize(fileInfo.getFileSize());
        fileInfoDTO.setTotalChunk(totalChunk);
        fileInfoDTO.setChunkSize(CHUNK_SIZE);
        return CompletableFuture.supplyAsync(() -> uploadId(fileInfoDTO),uploadExecutor);
    }

    private static String uploadId(FileInfoDTO fileInfoDTO){
        Result<String> uploadIdResult = okHttpClientHelper.doPost("/file/uploadId",fileInfoDTO, new TypeReference<>() {
        });
        return uploadIdResult.getData();
    }


    public static CompletableFuture<String> uploadFile(InputStream inputStream, String fileName, String fileType, int chunkSize,ProgressListener progressListener){
        try {
            long length = inputStream.available();
            int totalChunk = (int) Math.ceil((double) length / chunkSize);
            FileInfoDTO fileInfoDTO = new FileInfoDTO();
            fileInfoDTO.setFilename(fileName);
            fileInfoDTO.setFileType(fileType);
            fileInfoDTO.setTotalSize(length);
            fileInfoDTO.setTotalChunk(totalChunk);
            fileInfoDTO.setChunkSize(chunkSize);
            //前置获取uploadId
            return CompletableFuture.supplyAsync(() -> {
                return uploadId(fileInfoDTO);
                //多任务上传分片
            },uploadExecutor).thenCompose(uploadId -> {
                Log.i(TAG, "文件名称: "+ fileName+", 文件总大小:"+ convertBytesToMB(length)+", 总块数:"+totalChunk);
                List<CompletableFuture<Void>> futures = new ArrayList<>(totalChunk);
                try {
                    byte[] buffer;
                    for (int i = 0; i < totalChunk; i++) {
                        if (inputStream.available() <= chunkSize) {
                            buffer = new byte[inputStream.available()];
                        } else {
                            buffer = new byte[chunkSize];
                        }
                        int n = inputStream.read(buffer);
                        if (n == -1) {
                            break;
                        }
                        byte[] finalBuffer = buffer;
                        final int finalI = i;
                        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                            uploadChunk(finalBuffer, uploadId, length, totalChunk, chunkSize, finalI,progressListener);
                        },uploadExecutor);
                        futures.add(completableFuture);
                    }
                } catch (IOException ioException) {
                    Log.e(TAG,"UploadFile Exception ", ioException);
                    CompletableFuture<String> future = new CompletableFuture<>();
                    future.completeExceptionally(ioException);
                    return future;
                }finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG,"UploadFile Exception ",e);
                    }
                }
                //等待分片任务完成后获取访问的url
                return CompletableFuture
                        .allOf(futures.toArray(new CompletableFuture[0]))
                        .thenApply(t -> {
                            Result<String> result = okHttpClientHelper.doGet("/file/accessUrl?uploadId=" + uploadId, new TypeReference<>() {});
                            return result.getData();
                        });
            });
        }catch (Exception e){
            Log.e(TAG,"upload error: ",e);
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    private static void uploadChunk(byte[] chunkData, String uploadId, long totalSize, int totalChunk, int chunkSize, Integer chunkIndex,ProgressListener progressListener) {
        Log.i(TAG, "第"+(chunkIndex + 1)+"块正在上传, 当前块大小:"+convertBytesToMB(chunkData.length)+", 起始偏移量:"+(chunkIndex * chunkSize)+", 结束偏移量:"+(chunkIndex * chunkSize + chunkData.length));
        RequestBody requestBody = RequestBody.create(chunkData, MediaType.parse("application/octet-stream"));
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uploadId", uploadId)
                .addFormDataPart("totalSize", totalSize + "")
                .addFormDataPart("totalChunk", totalChunk + "")
                .addFormDataPart("chunkSize", chunkSize + "")
                .addFormDataPart("chunkIndex", chunkIndex + "")
                .addFormDataPart("file", "", requestBody)
                .build();
        okHttpClientHelper.doPost("/file/upload/chunk", multipartBody, new TypeReference<Void>() {});
        if (progressListener != null){
            progressListener.onProgress(chunkData.length);
        }
    }

    private static void simpleUpload(byte[] bytes, String fileName, String fileType,ProgressListener progressListener){
        Log.i(TAG, "simpleUpload 文件名称: "+fileName+" 文件大小:"+convertBytesToMB(bytes.length) + " 文件类型: " + fileType);
        RequestBody requestBody = RequestBody.create(bytes, MediaType.parse(fileType));
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file",fileName,requestBody)
                .build();
        okHttpClientHelper.doPost("/file/upload/simple", multipartBody, new TypeReference<Void>() {});
        if (progressListener != null){
            progressListener.onProgress(bytes.length);
        }
    }


    private static String convertBytesToMB(long size) {
        return String.format(Locale.US,"%.2f", (double) size / (1024 * 1024)) + "MB";
    }

    public static class FileInfoDTO {
        //文件名称
        private String filename;

        private String fileType;

        //文件总大小
        private Long totalSize;

        //总块数
        private Integer totalChunk;

        //每块的大小(最后一块文件大小小于等于该值)
        private Integer chunkSize;

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public Long getTotalSize() {
            return totalSize;
        }

        public void setTotalSize(Long totalSize) {
            this.totalSize = totalSize;
        }

        public Integer getTotalChunk() {
            return totalChunk;
        }

        public void setTotalChunk(Integer totalChunk) {
            this.totalChunk = totalChunk;
        }

        public Integer getChunkSize() {
            return chunkSize;
        }

        public void setChunkSize(Integer chunkSize) {
            this.chunkSize = chunkSize;
        }
    }

}
