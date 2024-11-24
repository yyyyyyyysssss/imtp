package org.imtp.desktop.component;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.imtp.common.response.Result;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
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
@Slf4j
public class ChunkedUploader {

    private static final int CHUNK_SIZE = 1024 * 1024 * 5; // 5MB

    private static final OKHttpClientHelper okHttpClientHelper;

    private static final ExecutorService uploadExecutor;

    static {
        okHttpClientHelper = OKHttpClientHelper.getInstance();
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);
            private final String threadPrefixName = "upload-pool-thread-";
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(threadPrefixName + threadNumber.getAndIncrement());
                return thread;
            }
        };
        uploadExecutor = Executors.newFixedThreadPool(4,threadFactory);
    }

    public static CompletableFuture<String> uploadFile(String path) {
        return uploadFile(path, CHUNK_SIZE);
    }

    public static CompletableFuture<String> uploadFile(String path, int chunkSize) {
        if (path.startsWith("file:")) {
            path = path.substring(6);
        }
        File file = new File(path);
        try {
            String fileType = mediaType(file);
            return uploadFile(new FileInputStream(file),file.getName(),fileType,chunkSize);
        } catch (FileNotFoundException e) {
            log.error("upload error: ",e);
            throw new RuntimeException("upload error");
        }
    }

    public static CompletableFuture<String> uploadFile(InputStream inputStream,String fileName,String fileType){

        return uploadFile(inputStream,fileName,fileType,CHUNK_SIZE);
    }

    public static CompletableFuture<String> uploadFile(InputStream inputStream,String fileName,String fileType, int chunkSize){
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
                Result<String> uploadIdResult = okHttpClientHelper.doPost("/file/uploadId",fileInfoDTO, new TypeReference<>() {
                });
                return uploadIdResult.getData();
                //多任务上传分片
            },uploadExecutor).thenCompose(uploadId -> {
                log.info("文件名称:{}, 文件总大小:{}, 总块数:{}", fileName, convertBytesToMB(length), totalChunk);
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
                            uploadChunk(finalBuffer, uploadId, length, totalChunk, chunkSize, finalI);
                        },uploadExecutor);
                        futures.add(completableFuture);
                    }
                } catch (IOException ioException) {
                    log.error("UploadFile Exception ", ioException);
                    return CompletableFuture.failedFuture(ioException);
                }finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
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
            log.error("upload error: ",e);
            throw new RuntimeException("upload error");
        }
    }

    private static void uploadChunk(byte[] chunkData, String uploadId, long totalSize, int totalChunk, int chunkSize, Integer chunkIndex) {
        log.info("第{}块正在上传, 当前块大小:{}, 起始偏移量:{}, 结束偏移量:{}", chunkIndex + 1, convertBytesToMB(chunkData.length), chunkIndex * chunkSize, chunkIndex * chunkSize + chunkData.length);
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
        okHttpClientHelper.doPost("/file/upload/chunk", multipartBody, new TypeReference<Void>() {
        });
    }


    private static String convertBytesToMB(long size) {
        return String.format("%.2f", (double) size / (1024 * 1024)) + "MB";
    }

    private static String mediaType(File file) {
        try {
            return Files.probeContentType(file.toPath());
        } catch (IOException e) {
            log.error("probeContentType error ", e);
            return "unknown";
        }
    }

    @Getter
    @Setter
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
    }

}
