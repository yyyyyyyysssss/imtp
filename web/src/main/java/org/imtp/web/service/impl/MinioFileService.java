package org.imtp.web.service.impl;

import groovy.lang.Tuple2;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.imtp.web.config.BusinessException;
import org.imtp.web.config.idwork.IdGen;
import org.imtp.web.config.minio.MinioHelper;
import org.imtp.web.domain.dto.UploadChunkDTO;
import org.imtp.web.domain.entity.FileUpload;
import org.imtp.web.enums.FileUploadStatus;
import org.imtp.web.mapper.FileUploadMapper;
import org.imtp.web.service.FileService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.*;
import java.util.Date;
import java.util.UUID;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/19 10:19
 */
@Service
@Slf4j
public class MinioFileService implements FileService {

    private final String tmpdir = System.getProperty("java.io.tmpdir") + FileSystems.getDefault().getSeparator();

    private final String uploadPrefix = "upload:";

    private final int bufferSize = 4096;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private MinioHelper minioHelper;

    @Resource
    private FileUploadMapper fileUploadMapper;

    @Override
    public String uploadId(String filename,Long totalSize) {
        FileUpload fileUpload = FileUpload
                .builder()
                .id(IdGen.genId())
                .fileName(filename)
                .totalSize(totalSize)
                .status(FileUploadStatus.PENDING)
                .createTime(new Date())
                .build();
        fileUploadMapper.insert(fileUpload);
        return fileUpload.getId().toString();
    }

    @Override
    public boolean upload(UploadChunkDTO uploadChunkDTO) {
        String uploadId = uploadChunkDTO.getUploadId();
        Long totalSize = uploadChunkDTO.getTotalSize();
        Long totalChunk = uploadChunkDTO.getTotalChunk();
        Integer chunkIndex = uploadChunkDTO.getChunkIndex();
        Long chunkSize = uploadChunkDTO.getChunkSize();
        MultipartFile file = uploadChunkDTO.getFile();
        InputStream inputStream = null;
        log.info("uploadId:{}, totalSize:{}, totalChunk:{}, chunkIndex:{}, chunkSize:{}", uploadId, totalSize, totalChunk, chunkIndex, chunkSize);
        //写入临时文件
        String tmpFilePath = tmpdir + uploadId + ".tmp";
        try (RandomAccessFile raf = new RandomAccessFile(tmpFilePath, "rw")) {
            raf.setLength(totalSize);
            raf.seek(chunkIndex * chunkSize);
            inputStream = file.getInputStream();
            byte[] buffer = new byte[bufferSize];
            int n;
            while ((n = inputStream.read(buffer)) != -1) {
                raf.write(buffer, 0, n);
            }
        } catch (IOException e) {
            log.error("upload error: ", e);
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("upload close InputStream error: ", e);
                }
            }
        }
        //获取已上传的块数
        Long uploadedChunkNum = redisTemplate.opsForValue().increment(uploadPrefix + uploadId);
        if (totalChunk.equals(uploadedChunkNum)) {
            FileUpload fileUpload = fileUploadMapper.selectById(uploadId);
            if (fileUpload == null){
                log.warn("任务不存在");
                return false;
            }
            String filename = fileUpload.getFileName();
            String fileSuffix = filename.substring(filename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString().replaceAll("-","") + fileSuffix;
            String newFilePath = tmpdir + newFilename;
            //修改文件名称
            try {
                Path tmpPath = Paths.get(tmpFilePath);
                Path path = Paths.get(newFilePath);
                Files.move(tmpPath, path, StandardCopyOption.REPLACE_EXISTING);
                //上传到minio
                Tuple2<String, String> upload = minioHelper.upload(newFilePath, newFilename);
                log.info("upload minio success; filename:{}, accessUrl:{}", newFilename, upload.getV2());
                fileUpload.setEtag(upload.getV1());
                fileUpload.setAccessUrl(upload.getV2());
                fileUpload.setStatus(FileUploadStatus.COMPLETED);
                redisTemplate.delete(uploadPrefix + uploadId);
                //上传完成删除临时文件
                Files.delete(path);
            } catch (IOException e) {
                log.error("upload  Files.move error: ", e);
                fileUpload.setStatus(FileUploadStatus.FAILED);
                return false;
            }
            fileUploadMapper.updateById(fileUpload);
        }
        return true;
    }

    @Override
    public String accessUrl(String uploadId) {
        try (HintManager hintManager = HintManager.getInstance()){
            hintManager.setWriteRouteOnly();
            FileUpload fileUpload = fileUploadMapper.selectById(uploadId);
            if (fileUpload == null){
                throw new BusinessException("该任务不存在: " + uploadId);
            }
            if (!fileUpload.getStatus().equals(FileUploadStatus.COMPLETED)){
                throw new BusinessException("任务未完成: " + uploadId);
            }
            return fileUpload.getAccessUrl();
        }
    }
}
