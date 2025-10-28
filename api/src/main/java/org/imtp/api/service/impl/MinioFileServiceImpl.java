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
import org.imtp.api.domain.vo.FileStreamVO;
import org.imtp.api.enums.FileStorageType;
import org.imtp.api.mapper.FileUploadMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    public String generateTemporaryUrl(String uploadId, Duration duration) {
        QueryWrapper<FileUpload> fileUploadQueryWrapper = new QueryWrapper<>();
        fileUploadQueryWrapper.select("id,file_name,access_url,original_url");
        fileUploadQueryWrapper.eq("upload_id", uploadId);
        FileUpload fileUpload = fileUploadMapper.selectOne(fileUploadQueryWrapper);
        if (fileUpload == null) {
            throw new BusinessException("该上传任务不存在: " + uploadId);
        }
        String originalUrl = fileUpload.getOriginalUrl();
        String objectName = originalUrl.substring(originalUrl.lastIndexOf("/") + 1);
        return minioHelper.generateTemporaryAccessUrl(objectName, duration);
    }

    @Override
    public Tuple2<String, String> simpleUpload(InputStream inputStream, String filename, String contentType, Long size) {
        return minioHelper.upload(inputStream, filename, contentType, size);
    }

    @Override
    public FileStreamVO getFileStream(String bucketName, String objectName, FileRangeDTO range) {
        Map<String, String> headerMap = new HashMap<>();
        GetObjectResponse objectResponse;
        // 如果没有指定范围，则直接下载整个文件
        if (range == null) {
            objectResponse = minioHelper.download(bucketName, objectName);
            objectResponse.headers().forEach(h -> headerMap.put(h.getFirst(), h.getSecond()));
            return new FileStreamVO(outputStream -> streamFile(objectResponse, outputStream), headerMap);
        }
        //指定范围时 先获取文件信息
        StatObjectResponse statObjectResponse = minioHelper.statObject(bucketName, objectName);
        long size = statObjectResponse.size();
        //验证范围
        validateRange(range, size);

        //如果范围是 -1 则表示从开始到结束
        long start = range.getStart();
        long end = range.getEnd() == -1 ? size - 1 : range.getEnd();
        long length = end - start + 1;
        //设置请求头
        headerMap.put(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + size);
        headerMap.put(HttpHeaders.CONTENT_LENGTH, String.valueOf(length));
        headerMap.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        //读取
        GetObjectResponse rangeObjectResponse = minioHelper.download(bucketName, objectName, start, length);

        return new FileStreamVO(outputStream -> streamFile(rangeObjectResponse, outputStream), headerMap);
    }

    private void validateRange(FileRangeDTO range, long size) {
        if (range.getStart() < 0 ||
                (range.getEnd() != -1 && (range.getEnd() >= size || range.getEnd() < range.getStart()))) {
            throw new BusinessException("Invalid range: The range exceeds the file size.");
        }
    }

    @Override
    public String pathSeparator() {
        return "/";
    }

}
