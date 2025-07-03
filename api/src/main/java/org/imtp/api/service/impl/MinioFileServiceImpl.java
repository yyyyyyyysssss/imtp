package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import groovy.lang.Tuple2;
import io.minio.GetObjectResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.config.minio.MinioHelper;
import org.imtp.api.domain.entity.FileUpload;
import org.imtp.api.enums.FileStorageType;
import org.imtp.api.mapper.FileUploadMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/19 10:19
 */
@Service("minioFileService")
@Slf4j
public class MinioFileServiceImpl extends AbstractFileService {

    @Resource
    MinioHelper minioHelper;

    @Resource
    FileUploadMapper fileUploadMapper;

    @Override
    public FileStorageType fileStorageType() {
        return FileStorageType.MINIO;
    }

    @Override
    public String uploadId(String filename,String fileType) {
        return minioHelper.uploadId(filename,fileType);
    }

    @Override
    public String storePart(String uploadId, InputStream inputStream, String filename, Long chunkSize, Integer chunkIndex, Long partSize) {
        return minioHelper.uploadPart(uploadId, inputStream, filename, chunkIndex, partSize);
    }

    @Override
    public Tuple2<String, String> mergePart(String uploadId, String filename, Integer totalChunk) {
        return minioHelper.mergePart(uploadId, filename, totalChunk);
    }

    @Override
    public String temporaryUrl(String uploadId, Duration duration) {
        QueryWrapper<FileUpload> fileUploadQueryWrapper = new QueryWrapper<>();
        fileUploadQueryWrapper.select("id,file_name,access_url");
        fileUploadQueryWrapper.eq("upload_id",uploadId);
        FileUpload fileUpload = fileUploadMapper.selectOne(fileUploadQueryWrapper);
        if (fileUpload == null){
            throw new BusinessException("该上传任务不存在: " + uploadId);
        }
        String accessUrl = fileUpload.getAccessUrl();
        String objectName = accessUrl.substring(accessUrl.lastIndexOf("/") + 1);
        return minioHelper.getTemporaryAccessUrl(objectName,duration);
    }

    @Override
    public Tuple2<String, String> simpleUpload(InputStream inputStream, String filename, String contentType, Long size) {
        return minioHelper.upload(inputStream,filename,contentType,size);
    }

    @Override
    public Tuple2<StreamingResponseBody, Map<String,String>> getFileStream(String bucketName, String objectName) {
        GetObjectResponse response = minioHelper.download(bucketName,objectName);
        Map<String,String> headerMap = new HashMap<>();
        response.headers().forEach(h -> headerMap.put(h.getFirst(), h.getSecond()));
        StreamingResponseBody responseBody = outputStream -> {
            try (InputStream inputStream = response) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                // 处理异常
                log.error("Error while streaming file: {}", e.getMessage(), e);
                throw new BusinessException("Error while streaming file: " + e.getMessage());
            }
        };
        return new Tuple2<>(responseBody, headerMap);
    }

}
