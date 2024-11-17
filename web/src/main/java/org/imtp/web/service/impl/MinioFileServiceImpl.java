package org.imtp.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import groovy.lang.Tuple2;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.web.config.exception.BusinessException;
import org.imtp.web.config.idwork.IdGen;
import org.imtp.web.config.minio.MinioHelper;
import org.imtp.web.domain.entity.FileUpload;
import org.imtp.web.enums.FileStorageType;
import org.imtp.web.enums.FileUploadStatus;
import org.imtp.web.mapper.FileUploadMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/19 10:19
 */
@Service("minioFileService")
@Slf4j
public class MinioFileServiceImpl extends AbstractFileService {

    @Resource
    private MinioHelper minioHelper;

    @Resource
    private FileUploadMapper fileUploadMapper;

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
    public String temporaryUrl(String uploadId) {
        QueryWrapper<FileUpload> fileUploadQueryWrapper = new QueryWrapper<>();
        fileUploadQueryWrapper.select("id,file_name,access_url");
        fileUploadQueryWrapper.eq("upload_id",uploadId);
        FileUpload fileUpload = fileUploadMapper.selectOne(fileUploadQueryWrapper);
        if (fileUpload == null){
            throw new BusinessException("该上传任务不存在: " + uploadId);
        }
        String accessUrl = fileUpload.getAccessUrl();
        String objectName = accessUrl.substring(accessUrl.lastIndexOf("/") + 1);
        return minioHelper.temporaryUrl(objectName);
    }

    @Override
    public Tuple2<String, String> simpleUpload(InputStream inputStream, String filename, String contentType, Long size) {
        return minioHelper.upload(inputStream,filename,contentType,size);
    }
}
