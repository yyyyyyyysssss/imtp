package org.imtp.client.component;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.imtp.common.response.Result;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
        //前置获取uploadId
        return CompletableFuture.supplyAsync(() -> {
            Result<String> uploadIdResult = okHttpClientHelper.doGet("/file/uploadId?filename=" + file.getName() + "&totalSize=" + file.length(), new TypeReference<>() {
            });
            return uploadIdResult.getData();
        //多任务上传分片
        },uploadExecutor).thenCompose(uploadId -> {
            long length = file.length();
            int totalChunk = (int) Math.ceil((double) length / chunkSize);
            log.info("文件名称:{}, 文件总大小:{}, 总块数:{}", file.getName(), convertBytesToMB(length), totalChunk);
            List<CompletableFuture<Void>> futures = new ArrayList<>(totalChunk);
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer;
                for (int i = 0; i < totalChunk; i++) {
                    if (fis.available() <= chunkSize) {
                        buffer = new byte[fis.available()];
                    } else {
                        buffer = new byte[chunkSize];
                    }
                    int n = fis.read(buffer);
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
            }
            //等待分片任务完成后获取访问的url
            return CompletableFuture
                    .allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(t -> {
                        Result<String> result = okHttpClientHelper.doGet("/file/accessUrl?uploadId=" + uploadId, new TypeReference<>() {});
                        return result.getData();
                    });
        });
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
        okHttpClientHelper.doPost("/file/upload", multipartBody, new TypeReference<Void>() {
        });
    }


    private static String convertBytesToMB(long size) {
        return String.format("%.2f", (double) size / (1024 * 1024)) + "MB";
    }

}
