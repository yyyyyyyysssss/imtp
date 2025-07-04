package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import groovy.lang.Tuple2;
import io.minio.GetObjectResponse;
import io.minio.StatObjectResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.config.minio.MinioHelper;
import org.imtp.api.domain.dto.FileRangeDTO;
import org.imtp.api.domain.entity.FileUpload;
import org.imtp.api.enums.FileStorageType;
import org.imtp.api.mapper.FileUploadMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
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
    public String uploadId(String filename, String fileType) {
        return minioHelper.uploadId(filename, fileType);
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
        fileUploadQueryWrapper.select("id,file_name,access_url,original_url");
        fileUploadQueryWrapper.eq("upload_id", uploadId);
        FileUpload fileUpload = fileUploadMapper.selectOne(fileUploadQueryWrapper);
        if (fileUpload == null) {
            throw new BusinessException("该上传任务不存在: " + uploadId);
        }
        String originalUrl = fileUpload.getOriginalUrl();
        String objectName = originalUrl.substring(originalUrl.lastIndexOf("/") + 1);
        return minioHelper.getTemporaryAccessUrl(objectName, duration);
    }

    @Override
    public Tuple2<String, String> simpleUpload(InputStream inputStream, String filename, String contentType, Long size) {
        return minioHelper.upload(inputStream, filename, contentType, size);
    }

    @Override
    public Tuple2<StreamingResponseBody, Map<String, String>> getFileStream(String bucketName, String objectName, FileRangeDTO range) {
        Map<String, String> headerMap = new HashMap<>();
        GetObjectResponse objectResponse;
        StatObjectResponse statObjectResponse;
        // 如果没有指定范围，则直接下载整个文件
        if (range == null) {
            objectResponse = minioHelper.download(bucketName, objectName);
            objectResponse.headers().forEach(h -> headerMap.put(h.getFirst(), h.getSecond()));
            statObjectResponse = null;
        } else {
            objectResponse = null;
            statObjectResponse = minioHelper.statObject(bucketName, objectName);
            long size = statObjectResponse.size();
            if (range.getStart() < 0 || (range.getEnd() != -1 && range.getEnd() >= size)) {
                throw new BusinessException("Invalid range: The range exceeds the file size.");
            }
            headerMap.put(HttpHeaders.CONTENT_RANGE, "bytes " + range.getStart() + "-" + (range.getEnd() == -1 ? size - 1 : range.getEnd()) + "/" + size);
            long length;
            if (range.getEnd() == -1) {
                length = size - range.getStart();
            } else {
                length = range.getEnd() - range.getStart() + 1;
            }
            headerMap.put(HttpHeaders.CONTENT_LENGTH, String.valueOf(length));
        }
        StreamingResponseBody responseBody = outputStream -> {
            if (range != null) {
                long offset = range.getStart();
                long length;
                // 如果 range.getEnd() 为 -1，表示下载到文件末尾
                if (range.getEnd() == -1) {
                    length = statObjectResponse.size() - range.getStart() + 1;
                } else {
                    length = range.getEnd() - range.getStart() + 1;
                }
                GetObjectResponse rangeObjectResponse = minioHelper.download(bucketName, objectName, offset, length);
                streamFile(rangeObjectResponse, outputStream);
            } else {
                streamFile(objectResponse, outputStream);
            }
        };
        return new Tuple2<>(responseBody, headerMap);
    }


    @Override
    public String pathSeparator() {
        return "/";
    }

}
