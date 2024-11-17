package org.imtp.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import groovy.lang.Tuple2;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.imtp.web.config.exception.BusinessException;
import org.imtp.web.config.exception.DatabaseException;
import org.imtp.web.config.idwork.IdGen;
import org.imtp.web.config.redis.RedisWrapper;
import org.imtp.web.domain.dto.FileChunkDTO;
import org.imtp.web.domain.dto.FileInfoDTO;
import org.imtp.web.domain.entity.FileUpload;
import org.imtp.web.domain.vo.FileUploadProgressVO;
import org.imtp.web.enums.FileStorageType;
import org.imtp.web.enums.FileUploadStatus;
import org.imtp.web.mapper.FileUploadMapper;
import org.imtp.web.service.FileService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Description
 * @Author ys
 * @Date 2024/11/17 10:28
 */
@Slf4j
public abstract class AbstractFileService implements FileService {

    @Resource
    protected FileUploadMapper fileUploadMapper;

    private final String uploadPrefix = "upload:";

    private final String totalChunkField = "totalChunk";
    private final String uploadedChunkCountField = "uploadedChunkCount";
    private final String newFilenameField = "newFilenameField";

    @Resource
    private RedisWrapper redisWrapper;

    @Override
    public String uploadId(FileInfoDTO fileInfoDTO) {
        FileUpload fileUpload = FileUpload
                .builder()
                .id(IdGen.genId())
                .fileName(fileInfoDTO.getFilename())
                .fileType(fileInfoDTO.getFileType())
                .totalSize(fileInfoDTO.getTotalSize())
                .totalChunk(fileInfoDTO.getTotalChunk())
                .chunkSize(fileInfoDTO.getChunkSize())
                .uploadedChunkCount(0)
                .status(FileUploadStatus.PENDING)
                .storageType(fileStorageType())
                .createTime(new Date())
                .build();
        String newFilename = newFilename(fileInfoDTO.getFilename());
        String fileType = StringUtils.isEmpty(fileInfoDTO.getFileType()) ?  "application/octet-stream" : fileInfoDTO.getFileType();
        String uploadId = uploadId(newFilename,fileType);
        fileUpload.setUploadId(uploadId);
        int i = fileUploadMapper.insert(fileUpload);
        if (i == 0){
            throw new DatabaseException("文件上传落库失败");
        }
        Map<String,Object> map = new HashMap<>();
        map.put(totalChunkField,fileInfoDTO.getTotalChunk());
        map.put(uploadedChunkCountField,0);
        map.put(newFilenameField,newFilename);
        redisWrapper.addHash(uploadPrefix + uploadId,map);
        return uploadId;
    }

    public abstract String uploadId(String filename,String fileType);

    public abstract FileStorageType fileStorageType();

    public abstract String storePart(String uploadId,InputStream inputStream,String filename,Long chunkSize,Integer chunkIndex,Long partSize);

    public abstract Tuple2<String, String> mergePart(String uploadId, String filename, Integer totalChunk);

    @Override
    @Transactional(noRollbackFor = Exception.class)
    public boolean uploadChunk(FileChunkDTO fileChunkDTO) {
        String uploadId = fileChunkDTO.getUploadId();
        Long totalSize = fileChunkDTO.getTotalSize();
        Long totalChunk = fileChunkDTO.getTotalChunk();
        Integer chunkIndex = fileChunkDTO.getChunkIndex();
        Long chunkSize = fileChunkDTO.getChunkSize();
        MultipartFile file = fileChunkDTO.getFile();
        String filename = (String)redisWrapper.getHash(uploadPrefix + uploadId, newFilenameField);
        log.debug("uploadId:{}, totalSize:{}, totalChunk:{}, chunkIndex:{}, chunkSize:{}, partSize:{}", uploadId, totalSize, totalChunk, chunkIndex, chunkSize,file.getSize());
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            storePart(uploadId,inputStream,filename,chunkSize,chunkIndex,file.getSize());
            //获取已上传的块数
            Long uploadedChunkNum = redisWrapper.incrHash(uploadPrefix + uploadId, uploadedChunkCountField);
            if (log.isDebugEnabled()){
                String progress = calculateProgress(uploadedChunkNum, totalChunk);
                log.debug("上传进度:{}, totalChunk:{}, uploadedChunkNum:{}",progress,totalChunk,uploadedChunkNum);
            }
            if (totalChunk.equals(uploadedChunkNum)){
                Tuple2<String, String> tuple2 = mergePart(uploadId, filename, totalChunk.intValue());

                String etag = tuple2.getV1();
                String accessUrl = tuple2.getV2();
                UpdateWrapper<FileUpload> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("access_url",accessUrl);
                updateWrapper.set("uploaded_chunk_count",uploadedChunkNum);
                updateWrapper.set("status",FileUploadStatus.COMPLETED);
                updateWrapper.set("etag",etag);
                updateWrapper.eq("upload_id",uploadId);
                fileUploadMapper.update(null, updateWrapper);

                redisWrapper.expire(uploadPrefix + uploadId, Duration.ofMinutes(5));
            }
        } catch (Exception e) {
            Object uploadedChunkNum = redisWrapper.getHash(uploadPrefix + uploadId, uploadedChunkCountField);
            UpdateWrapper<FileUpload> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("status",FileUploadStatus.FAILED);
            updateWrapper.set("uploaded_chunk_count",uploadedChunkNum);
            updateWrapper.eq("upload_id",uploadId);
            fileUploadMapper.update(null, updateWrapper);

            redisWrapper.expire(uploadPrefix + uploadId, Duration.ofDays(3));
            throw new BusinessException(e);
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("upload close InputStream error: ", e);
                }
            }
        }
        return true;
    }

    private String newFilename(String originFilename){
        if (StringUtils.isEmpty(originFilename)){
            throw new NullPointerException("文件名称不可为空");
        }
        String fileSuffix = originFilename.substring(originFilename.lastIndexOf("."));
        return UUID.randomUUID().toString().replaceAll("-","") + fileSuffix;
    }

    private String calculateProgress(long uploadedChunkNum,long totalChunk) {
        double d = (double) uploadedChunkNum / totalChunk * 100;
        return String.format("%.2f%%",d);
    }

    @Override
    public FileUploadProgressVO uploadProgress(String uploadId) {
        FileUploadProgressVO fileUploadProgressVO = new FileUploadProgressVO();
        fileUploadProgressVO.setUploadId(uploadId);
        Map<String, Object> map = redisWrapper.getHashAll(uploadPrefix + uploadId);
        if (map != null && !map.isEmpty()){
            Integer totalChunk = (Integer) map.get(totalChunkField);
            Integer uploadedChunkCount = (Integer) map.get(uploadedChunkCountField);
            fileUploadProgressVO.setTotalChunk(totalChunk);
            fileUploadProgressVO.setUploadedChunkCount(uploadedChunkCount);
        }else {
            QueryWrapper<FileUpload> fileUploadQueryWrapper = new QueryWrapper<>();
            fileUploadQueryWrapper.select("id,total_chunk,uploaded_chunk_count");
            fileUploadQueryWrapper.eq("upload_id",uploadId);
            FileUpload fileUpload = fileUploadMapper.selectOne(fileUploadQueryWrapper);
            if (fileUpload == null){
                throw new BusinessException("该上传任务不存在: " + uploadId);
            }
            fileUploadProgressVO.setTotalChunk(fileUpload.getTotalChunk());
            fileUploadProgressVO.setUploadedChunkCount(fileUpload.getUploadedChunkCount());
        }
        return fileUploadProgressVO;
    }


    @Override
    public String accessUrl(String uploadId) {
        try (HintManager hintManager = HintManager.getInstance()){
            hintManager.setWriteRouteOnly();
            QueryWrapper<FileUpload> fileUploadQueryWrapper = new QueryWrapper<>();
            fileUploadQueryWrapper.select("access_url","status");
            fileUploadQueryWrapper.eq("upload_id",uploadId);
            FileUpload fileUpload = fileUploadMapper.selectOne(fileUploadQueryWrapper);
            if (fileUpload == null){
                throw new BusinessException("该上传任务不存在: " + uploadId);
            }
            if (!fileUpload.getStatus().equals(FileUploadStatus.COMPLETED)){
                throw new BusinessException("该上传任务未完成: " + uploadId);
            }
            return fileUpload.getAccessUrl();
        }
    }

    public abstract Tuple2<String, String> simpleUpload(InputStream inputStream,String filename,String contentType,Long size);

    @Override
    public String simpleUpload(MultipartFile file) {
        FileUpload fileUpload = FileUpload
                .builder()
                .id(IdGen.genId())
                .uploadId(UUID.randomUUID().toString().replaceAll("-",""))
                .storageType(fileStorageType())
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .totalSize(file.getSize())
                .totalChunk(1)
                .uploadedChunkCount(1)
                .chunkSize((int)file.getSize())
                .createTime(new Date())
                .build();
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            String filename = newFilename(file.getOriginalFilename());
            String contentType = file.getContentType();
            long size = file.getSize();
            Tuple2<String, String> tuple2 = simpleUpload(inputStream, filename, contentType, size);
            String etag = tuple2.getV1();
            String accessUrl = tuple2.getV2();
            fileUpload.setEtag(etag);
            fileUpload.setAccessUrl(accessUrl);
            fileUpload.setStatus(FileUploadStatus.COMPLETED);
            fileUploadMapper.insert(fileUpload);
            return accessUrl;
        }catch (Exception e){
            log.error("upload error: ",e);
            fileUpload.setStatus(FileUploadStatus.FAILED);
            fileUploadMapper.insert(fileUpload);
            throw new BusinessException(e);
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("upload close InputStream error: ", e);
                }
            }
        }
    }
}
