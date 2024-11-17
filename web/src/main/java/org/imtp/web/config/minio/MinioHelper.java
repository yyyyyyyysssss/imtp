package org.imtp.web.config.minio;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import groovy.lang.Tuple2;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Part;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.web.config.exception.MinioException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/19 13:37
 */
@Component
@Slf4j
public class MinioHelper extends MinioAsyncClient {

    @Resource
    private MinioClient minioClient;

    @Resource
    private MinioConfig minioConfig;

    public MinioHelper(MinioAsyncClient minioAsyncClient) {
        super(minioAsyncClient);
    }

    public String uploadId(String filename,String fileType) {
        try {
            Multimap<String,String> header = HashMultimap.create();
            header.put("Content-Type",fileType);
            CompletableFuture<CreateMultipartUploadResponse> multipartUploadAsync = this.createMultipartUploadAsync(
                    minioConfig.getBucketName(),
                    null,
                    filename,
                    header,
                    null
            );
            return multipartUploadAsync.get().result().uploadId();
        } catch (Exception e) {
            log.error("获取minio uploadId异常: ", e);
            throw new MinioException("获取uploadId异常");
        }
    }

    public String uploadPart(String uploadId,InputStream inputStream,String filename,Integer chunkIndex,Long partSize) throws MinioException{
        try {
            CompletableFuture<UploadPartResponse> completableFuture = this.uploadPartAsync(
                    minioConfig.getBucketName(),
                    null,
                    filename,
                    inputStream,
                    partSize,
                    uploadId,
                    chunkIndex + 1,
                    null,
                    null
            );
            UploadPartResponse uploadPartResponse = completableFuture.get();
            return uploadPartResponse.etag();
        } catch (Exception e) {
            throw new MinioException("minio分片上传异常:" + e.getMessage());
        }
    }

    public Tuple2<String, String> mergePart(String uploadId,String filename,Integer totalChunk) throws MinioException{
        List<Part> parts = listParts(uploadId, filename ,totalChunk);
        try {
            CompletableFuture<ObjectWriteResponse> completableFuture = this.completeMultipartUploadAsync(
                    minioConfig.getBucketName(),
                    null,
                    filename,
                    uploadId,
                    parts.toArray(new Part[]{}),
                    null,
                    null
            );
            ObjectWriteResponse objectWriteResponse = completableFuture.get();
            String accessUrl = getAccessUrl(filename);
            return new Tuple2<>(objectWriteResponse.etag(), accessUrl);
        } catch (Exception e) {
            throw new MinioException("minio分片合并异常:" + e.getMessage());
        }
    }

    public List<Part> listParts(String uploadId,String filename,Integer totalChunk) throws MinioException{
        try {
            CompletableFuture<ListPartsResponse> completableFuture = this.listPartsAsync(
                    minioConfig.getBucketName(),
                    null,
                    filename,
                    totalChunk,
                    0,
                    uploadId,
                    null,
                    null
            );
            ListPartsResponse listPartsResponse = completableFuture.get();
            return listPartsResponse.result().partList();
        } catch (Exception e) {
            throw new MinioException("minio获取分片异常:" + e.getMessage());
        }
    }


    public Tuple2<String, String> upload(String filepath, String filename) throws MinioException{
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs
                    .builder()
                    .bucket(minioConfig.getBucketName())
                    .object(filename)
                    .filename(filepath)
                    .build();
            ObjectWriteResponse objectWriteResponse = minioClient.uploadObject(uploadObjectArgs);
            String accessUrl = getAccessUrl(filename);
            return new Tuple2<>(objectWriteResponse.etag(), accessUrl);
        } catch (Exception e) {
            log.error("uploadMinio error: ", e);
            throw new MinioException("minio根据路径上传异常: " + filepath);
        }
    }

    public Tuple2<String, String> upload(InputStream inputStream,String filename,String contentType,Long size) throws MinioException{
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs
                    .builder()
                    .bucket(minioConfig.getBucketName())
                    .contentType(contentType)
                    .object(filename)
                    .stream(inputStream, size, -1)
                    .build();
            ObjectWriteResponse objectWriteResponse = minioClient.putObject(putObjectArgs);
            String accessUrl = getAccessUrl(filename);
            return new Tuple2<>(objectWriteResponse.etag(), accessUrl);
        } catch (Exception e) {
            log.error("uploadMinio error: ", e);
            throw new MinioException("minio根据MultipartFile上传异常: " + filename);
        }
    }

    public String temporaryUrl(String name) throws MinioException{

        return temporaryUrl(name, Duration.ofDays(3));
    }

    public String temporaryUrl(String name, Duration duration) throws MinioException{
        try {
            GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs
                    .builder()
                    .method(Method.GET)
                    .bucket(minioConfig.getBucketName())
                    .expiry((int) duration.toSeconds())
                    .object(name)
                    .build();
            return minioClient.getPresignedObjectUrl(args);
        } catch (Exception e) {
            log.error("timedPreviewUrl error: ", e);
            throw new MinioException(e.getMessage());
        }
    }

    public String timedDownloadUrl(String name) throws MinioException{
        try {
            Map<String, String> map = new HashMap<>();
            map.put("response-content-type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
            GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs
                    .builder()
                    .method(Method.GET)
                    .bucket(minioConfig.getBucketName())
                    .object(name)
                    .extraQueryParams(map)
                    .build();
            return minioClient.getPresignedObjectUrl(args);
        } catch (Exception e) {
            log.error("timedDownloadUrl error: ", e);
            throw new MinioException("获取限时文件访问url异常: " + name);
        }
    }


    private String getAccessUrl(String filename) {
        return minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + filename;
    }

}
