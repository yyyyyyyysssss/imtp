package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import groovy.lang.Tuple2;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.config.exception.DatabaseException;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.config.redis.RedisHelper;
import org.imtp.api.domain.dto.FileChunkDTO;
import org.imtp.api.domain.dto.FileInfoDTO;
import org.imtp.api.domain.entity.FileUpload;
import org.imtp.api.domain.vo.FileInfoVO;
import org.imtp.api.domain.vo.FileUploadChunkVO;
import org.imtp.api.domain.vo.FileUploadProgressVO;
import org.imtp.api.enums.FileStorageType;
import org.imtp.api.enums.FileUploadStatus;
import org.imtp.api.mapper.FileUploadMapper;
import org.imtp.api.service.FileService;
import org.imtp.api.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

/**
 * @Description
 * @Author ys
 * @Date 2024/11/17 10:28
 */
@Slf4j
public abstract class AbstractFileService implements FileService {

    @Resource
    protected FileUploadMapper fileUploadMapper;

    protected final int bufferSize = 8192;

    private final String uploadPrefix = "upload:";

    private final String totalChunkField = "totalChunk";
    private final String totalSizeField = "totalSize";
    private final String uploadedChunkCountField = "uploadedChunkCount";
    private final String newFilenameField = "newFilenameField";
    private final String accessUrlField = "accessUrlField";

    @Value("${api.endpoint}")
    String apiEndpoint;

    @Resource
    private RedisHelper redisHelper;

    @Resource
    private ExecutorService defaultThreadPool;


    @Override
    @Cacheable(value = "file:upload:check", key = "#md5", unless="#result == null")
    public String checkMD5(String md5){
        QueryWrapper<FileUpload> fileUploadQueryWrapper = new QueryWrapper<>();
        fileUploadQueryWrapper
                .lambda()
                .select(FileUpload::getAccessUrl)
                .eq(FileUpload::getMd5,md5)
                .orderByDesc(FileUpload::getId)
                .last("limit 1");
        FileUpload fileUpload = fileUploadMapper.selectOne(fileUploadQueryWrapper);
        if(fileUpload != null){
            return fileUpload.getAccessUrl();
        }
        return null;
    }

    @Override
    public String getUploadId(FileInfoDTO fileInfoDTO) {
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
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
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
        map.put(totalSizeField,fileInfoDTO.getTotalSize());
        map.put(totalChunkField,fileInfoDTO.getTotalChunk());
        map.put(uploadedChunkCountField,0);
        map.put(newFilenameField,newFilename);
        map.put(accessUrlField,null);
        redisHelper.addHash(uploadPrefix + uploadId,map, Duration.ofHours(24));
        return uploadId;
    }

    public abstract String uploadId(String filename,String fileType);

    public abstract FileStorageType fileStorageType();

    public abstract String storePart(String uploadId,InputStream inputStream,String filename,Long chunkSize,Integer chunkIndex,Long partSize);

    public abstract Tuple2<String, String> mergePart(String uploadId, String filename, Integer totalChunk);

    public abstract String pathSeparator();

    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    public FileUploadChunkVO uploadChunk(FileChunkDTO fileChunkDTO) {
        String uploadId = fileChunkDTO.getUploadId();
        Integer chunkIndex = fileChunkDTO.getChunkIndex();
        Long chunkSize = fileChunkDTO.getChunkSize();
        MultipartFile file = fileChunkDTO.getFile();
        Map<String, Object> map = redisHelper.getHashAll(uploadPrefix + uploadId);
        if(map == null || map.isEmpty()){
            throw new BusinessException("上传任务不存在或已过期: " + uploadId);
        }
        String filename = (String) map.get(newFilenameField);
        Long totalSize = Long.parseLong(map.get(totalSizeField).toString());
        Long totalChunk = Long.parseLong(map.get(totalChunkField).toString());
        log.debug("uploadId:{}, totalSize:{}, totalChunk:{}, chunkIndex:{}, chunkSize:{}, partSize:{}", uploadId, totalSize, totalChunk, chunkIndex, chunkSize,file.getSize());
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            String chunkEtag = storePart(uploadId, inputStream, filename, chunkSize, chunkIndex, file.getSize());
            //获取已上传的块数
            Long uploadedChunkNum = redisHelper.incrHash(uploadPrefix + uploadId, uploadedChunkCountField);
            if (log.isDebugEnabled()){
                String progress = calculateProgress(uploadedChunkNum, totalChunk);
                log.debug("上传进度:{}, totalChunk:{}, uploadedChunkNum:{}",progress,totalChunk,uploadedChunkNum);
            }
            if (totalChunk.equals(uploadedChunkNum)){
                Tuple2<String, String> tuple2 = mergePart(uploadId, filename, totalChunk.intValue());

                String etag = tuple2.getV1();
                String originalUrl = tuple2.getV2();
                String accessUrl = createAccessUrl(originalUrl);
                UpdateWrapper<FileUpload> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("access_url",accessUrl);
                updateWrapper.set("original_url",originalUrl);
                updateWrapper.set("uploaded_chunk_count",uploadedChunkNum);
                updateWrapper.set("status",FileUploadStatus.COMPLETED);
                updateWrapper.set("etag",etag);
                updateWrapper.eq("upload_id",uploadId);
                fileUploadMapper.update(null, updateWrapper);
                redisHelper.addHash(uploadPrefix + uploadId,accessUrlField,accessUrl,Duration.ofMinutes(5));
                // 异步计算md5
                calculateMD5Async(uploadId,originalUrl);
            }
            FileUploadChunkVO fileUploadChunkVO = new FileUploadChunkVO();
            fileUploadChunkVO.setUploadId(uploadId);
            fileUploadChunkVO.setChunkIndex(chunkIndex);
            fileUploadChunkVO.setEtag(chunkEtag);
            fileUploadChunkVO.setUploadSize(file.getSize());
            return fileUploadChunkVO;
        } catch (Exception e) {
            Object uploadedChunkNum = redisHelper.getHash(uploadPrefix + uploadId, uploadedChunkCountField);
            UpdateWrapper<FileUpload> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("status",FileUploadStatus.FAILED);
            updateWrapper.set("uploaded_chunk_count",uploadedChunkNum);
            updateWrapper.eq("upload_id",uploadId);
            fileUploadMapper.update(null, updateWrapper);
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
    public FileUploadProgressVO getUploadProgress(String uploadId) {
        FileUploadProgressVO fileUploadProgressVO = new FileUploadProgressVO();
        fileUploadProgressVO.setUploadId(uploadId);
        Map<String, Object> map = redisHelper.getHashAll(uploadPrefix + uploadId);
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
    public String getAccessUrl(String uploadId) {
        String accessUrl = (String) redisHelper.getHash(uploadPrefix + uploadId, accessUrlField);
        if (accessUrl != null && !accessUrl.isEmpty()){
            return accessUrl;
        }
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

    public abstract Tuple2<String, String> simpleUpload(InputStream inputStream,String filename,String contentType,Long size);

    @Override
    public String uploadSingleFile(MultipartFile file) {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new BusinessException(e);
        }
        String filename = newFilename(file.getOriginalFilename());
        String contentType = file.getContentType();
        return uploadSingleFile(inputStream, filename, contentType);
    }

    @Override
    public String uploadSingleFile(InputStream inputStream, String fileName, String fileType) {
        FileUpload fileUpload = FileUpload
                .builder()
                .id(IdGen.genId())
                .uploadId(UUID.randomUUID().toString().replaceAll("-",""))
                .storageType(fileStorageType())
                .fileName(fileName)
                .fileType(fileType)
                .totalChunk(1)
                .uploadedChunkCount(1)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        try (InputStream in = inputStream){
            long size = inputStream.available();
            fileUpload.setTotalSize(size);
            fileUpload.setChunkSize((int)size);
            Tuple2<String, String> tuple2 = simpleUpload(in, fileName, fileType, size);
            String etag = tuple2.getV1();
            String originalUrl = tuple2.getV2();
            String accessUrl = createAccessUrl(originalUrl);
            fileUpload.setEtag(etag);
            fileUpload.setMd5(etag);
            fileUpload.setAccessUrl(accessUrl);
            fileUpload.setOriginalUrl(originalUrl);
            fileUpload.setStatus(FileUploadStatus.COMPLETED);
            fileUploadMapper.insert(fileUpload);
            return accessUrl;
        } catch (IOException e) {
            log.error("upload error: ",e);
            fileUpload.setStatus(FileUploadStatus.FAILED);
            fileUploadMapper.insert(fileUpload);
            throw new BusinessException(e);
        }
    }

    @Override
    public FileInfoVO getFileInfo(String bucketName, String objectName) {
        String accessUrl = createAccessUrl(bucketName + pathSeparator() + objectName);
        QueryWrapper<FileUpload> fileUploadQueryWrapper = new QueryWrapper<>();
        fileUploadQueryWrapper
                .lambda()
                .eq(FileUpload::getAccessUrl, accessUrl);
        FileUpload fileUpload = fileUploadMapper.selectOne(fileUploadQueryWrapper);
        if(fileUpload == null){
            throw new BusinessException("文件不存在或已被删除: " + accessUrl);
        }
        FileInfoVO fileInfoVO = new FileInfoVO();
        fileInfoVO.setFilename(fileUpload.getFileName());
        fileInfoVO.setFileType(fileUpload.getFileType());
        fileInfoVO.setTotalSize(fileUpload.getTotalSize());
        return fileInfoVO;
    }


    protected InputStream download(String fileUrl){
        String[] parsePath = parsePath(fileUrl);
        return download(parsePath[parsePath.length - 2],parsePath[parsePath.length - 1]);
    }

    protected void calculateMD5Async(String uploadId,String url){
        defaultThreadPool.execute(() -> {
            try (InputStream inputStream = download(url)){
                String md5 = MD5Utils.getMD5(inputStream);
                UpdateWrapper<FileUpload> fileUploadUpdateWrapper = new UpdateWrapper<>();
                fileUploadUpdateWrapper
                        .lambda()
                        .set(FileUpload::getMd5,md5)
                        .eq(FileUpload::getUploadId,uploadId);
                fileUploadMapper.update(null,fileUploadUpdateWrapper);
            }catch (Exception e){
                log.error("文件md5计算异常; uploadId: {}",uploadId,e);
            }
        });
    }

    protected void streamFile(InputStream is, OutputStream outputStream) {
        try (InputStream inputStream = is) {
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            if (isClientAbort(e)) {
                // 客户端主动断开，正常行为
                log.info("Client aborted file stream connection");
                return;
            }
            // 处理异常
            log.error("文件传输过程中发生错误: {}", e.getMessage());
        }
    }

    protected FileUpload getFileUploadByOriginUrl(String originUrl){
        if(originUrl == null || originUrl.isEmpty()){
            throw new NullPointerException("originUrl is null");
        }
        QueryWrapper<FileUpload> fileUploadQueryWrapper = new QueryWrapper<>();
        fileUploadQueryWrapper
                .lambda()
                .eq(FileUpload::getOriginalUrl,originUrl);
        return fileUploadMapper.selectOne(fileUploadQueryWrapper);
    }

    private boolean isClientAbort(Throwable e) {
        Throwable t = e;
        while (t != null) {
            if (t instanceof org.apache.catalina.connector.ClientAbortException) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }

    protected String createAccessUrl(String originalUrl) {
        if (StringUtils.isEmpty(originalUrl)) {
            throw new BusinessException("original url cannot be empty");
        }
        String[] parsePath = parsePath(originalUrl);
        return apiEndpoint + "/file/" + parsePath[parsePath.length - 2] + "/" + parsePath[parsePath.length - 1];
    }

    private String[] parsePath(String path) {

        return removeProtocol(path).split(Pattern.quote(pathSeparator()));
    }

    private String removeProtocol(String url) {
        try {
            URI uri = new URI(url);
            return uri.getPath();  // 获取去掉协议后的路径部分
        } catch (URISyntaxException e) {
            // 如果是无效的URL，可以返回原始字符串或根据需求处理
            return url;
        }
    }
}
