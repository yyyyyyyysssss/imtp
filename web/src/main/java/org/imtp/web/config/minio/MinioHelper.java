package org.imtp.web.config.minio;

import groovy.lang.Tuple2;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/19 13:37
 */
@Component
@Slf4j
public class MinioHelper {

    @Resource
    private MinioClient minioClient;

    @Resource
    private MinioConfig minioConfig;

    public Tuple2<String,String> upload(String filepath, String filename){
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs
                    .builder()
                    .bucket(minioConfig.getBucketName())
                    .object(filename)
                    .filename(filepath)
                    .build();
            ObjectWriteResponse objectWriteResponse = minioClient.uploadObject(uploadObjectArgs);
            String accessUrl = getAccessUrl(filename);
            return new Tuple2<>(objectWriteResponse.etag(),accessUrl);
        } catch (Exception e) {
            log.error("uploadMinio error: ",e);
            throw new RuntimeException(e);
        }
    }

    private Tuple2<String,String> upload(MultipartFile file) {
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs
                    .builder()
                    .bucket(minioConfig.getBucketName())
                    .contentType(file.getContentType())
                    .object(UUID.randomUUID().toString().replaceAll("-",""))
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .build();
            ObjectWriteResponse objectWriteResponse = minioClient.putObject(putObjectArgs);
            String accessUrl = getAccessUrl(file.getOriginalFilename());
            return new Tuple2<>(objectWriteResponse.etag(),accessUrl);
        }catch (Exception e){
            log.error("uploadMinio error: ",e);
            throw new RuntimeException(e);
        }
    }

    public String timedPreviewUrl(String name){
        try {
            GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs
                    .builder()
                    .method(Method.GET)
                    .bucket(minioConfig.getBucketName())
                    .object(name)
                    .build();
            return minioClient.getPresignedObjectUrl(args);
        }catch (Exception e){
            log.error("timedPreviewUrl error: ",e);
            throw new RuntimeException(e);
        }
    }

    public String timedDownloadUrl(String name){
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
        }catch (Exception e){
            log.error("timedDownloadUrl error: ",e);
            throw new RuntimeException(e);
        }
    }


    private String getAccessUrl(String filename){
        return minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + filename;
    }

}
