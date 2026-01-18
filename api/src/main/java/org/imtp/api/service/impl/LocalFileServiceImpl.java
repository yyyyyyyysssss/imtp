package org.imtp.api.service.impl;

import groovy.lang.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.domain.dto.FileRangeDTO;
import org.imtp.api.domain.entity.FileUpload;
import org.imtp.api.domain.vo.FileStreamVO;
import org.imtp.api.enums.FileStorageType;
import org.imtp.api.utils.MD5Utils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Description
 * @Author ys
 * @Date 2024/11/16 23:11
 */
@Service("localFileService")
@Slf4j
public class LocalFileServiceImpl extends AbstractFileService {

    private final String tmpdir = System.getProperty("java.io.tmpdir");

    private final String bucketName = "imtp-bucket";


    @Override
    public String getUploadId(String objectName, String fileType) {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Override
    public FileStorageType fileStorageType() {
        return FileStorageType.LOCAL;
    }

    @Override
    public String storePart(String uploadId, InputStream inputStream, String objectName, Long chunkSize, Integer chunkIndex, Long partSize) {
        String tmpFilePath = buildFilePath(objectName) + ".tmp";
        try (RandomAccessFile raf = new RandomAccessFile(tmpFilePath, "rw")) {
            raf.seek(chunkIndex * chunkSize);

            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[bufferSize];
            int n;
            while ((n = inputStream.read(buffer)) != -1) {
                raf.write(buffer, 0, n);
                md.update(buffer, 0, n);
            }
            return Hex.encodeHexString(md.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("upload error: ", e);
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("upload close InputStream error: ", e);
                }
            }
        }
    }

    @Override
    public Tuple2<String, String> mergePart(String uploadId, String objectName, Integer totalChunk) {
        String tmpFilePath = buildFilePath(objectName) + ".tmp";
        Path tmpPath = Paths.get(tmpFilePath);
        String filePath = buildFilePath(objectName);
        Path path = Paths.get(filePath);
        try {
            Files.move(tmpPath, path, StandardCopyOption.REPLACE_EXISTING);
            String etag = MD5Utils.getMD5(new File(filePath));
            log.info("upload success; objectName:{}, accessUrl:{}", objectName, filePath);
            return new Tuple2<>(etag, filePath);
        } catch (IOException e) {
            log.error("upload  Files.move error: ", e);
            throw new BusinessException(e);
        }
    }

    @Override
    public String generateTemporaryUrl(String uploadId, Duration duration) {
        throw new UnsupportedOperationException("本地文件暂不支持生成临时访问url");
    }

    @Override
    public InputStream download(String bucketName, String objectName) {
        String filePath = buildFilePath(bucketName,objectName);
        try {
            return new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileStreamVO getFileStream(String bucketName, String objectName, FileRangeDTO range) {
        Map<String, String> headerMap = new HashMap<>();
        try {
            FileUpload fileUpload = getFileUpload(bucketName,objectName);
            headerMap.put(HttpHeaders.ACCEPT_RANGES,"bytes");
            headerMap.put(HttpHeaders.CONTENT_TYPE, fileUpload.getFileType());
            headerMap.put(HttpHeaders.ETAG,fileUpload.getEtag());
            headerMap.put(HttpHeaders.LAST_MODIFIED,fileUpload.getUpdateTime().atZone(ZoneId.systemDefault()).toString());
            File file = new File(fileUpload.getOriginalUrl());
            long length = file.length();
            if (range != null) {
                if (range.getStart() < 0 || (range.getEnd() != -1 && range.getEnd() >= file.length())) {
                    throw new BusinessException("Invalid range: The range exceeds the file size.");
                }
                if (range.getEnd() == -1) {
                    length = file.length() - range.getStart();
                } else {
                    length = range.getEnd() - range.getStart() + 1;
                }
                //设置请求头
                headerMap.put(HttpHeaders.CONTENT_RANGE, "bytes " + range.getStart() + "-" + (range.getEnd() == -1 ? file.length() - 1 : range.getEnd()) + "/" + file.length());
            }
            headerMap.put(HttpHeaders.CONTENT_LENGTH, String.valueOf(length));
            StreamingResponseBody responseBody = outputStream -> {
                if (range != null) {
                    try (RandomAccessFile randomAccessFile = new RandomAccessFile(fileUpload.getOriginalUrl(), "r")) {
                        long start = range.getStart();
                        long len;
                        // 如果 range.getEnd() 为 -1，表示下载到文件末尾
                        if (range.getEnd() == -1) {
                            len = file.length() - range.getStart();
                        } else {
                            len = range.getEnd() - range.getStart();
                        }
                        randomAccessFile.seek(start);
                        byte[] buffer = new byte[bufferSize];
                        int bytesRead;
                        while (len > 0 && (bytesRead = randomAccessFile.read(buffer, 0, (int) Math.min(bufferSize, len))) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                            len -= bytesRead;
                        }
                    } catch (IOException e) {
                        log.error("getFileStream error: ", e);
                        throw new BusinessException("getFileStream error: " + e.getMessage());
                    }
                } else {
                    streamFile(new FileInputStream(fileUpload.getOriginalUrl()), outputStream);
                }
            };

            return new FileStreamVO(responseBody, headerMap);
        } catch (Exception e) {
            log.error("getFileStream error: ", e);
            throw new BusinessException("getFileStream error: " + e.getMessage());
        }
    }

    @Override
    public Tuple2<String, String> simpleUpload(InputStream inputStream, String objectName, String contentType, Long size) {
        String filePath = buildFilePath(objectName);
        FileOutputStream fileOutputStream = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            fileOutputStream = new FileOutputStream(filePath);
            byte[] buffer = new byte[bufferSize];
            int n;
            while ((n = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, n);
                md.update(buffer, 0, n);
            }
            byte[] md5Bytes = md.digest();
            String etag = Hex.encodeHexString(md5Bytes);
            return new Tuple2<>(etag, filePath);
        } catch (Exception e) {
            log.error("simpleUpload error: ", e);
            throw new BusinessException("simpleUpload error: " + e.getMessage());
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    log.error("simpleUpload error: ", e);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("simpleUpload error: ", e);
                }
            }
        }
    }

    @Override
    protected String bucketName() {
        return bucketName;
    }

    private String buildFilePath(String objectName){

        return buildFilePath(bucketName(),objectName);
    }

    private String buildFilePath(String bucketName, String objectName) {
        String pathSeparator = pathSeparator();
        String basePath = tmpdir + bucketName;
        String parentDirectory = new File(objectName).getParent();
        if(parentDirectory != null){
            if(parentDirectory.startsWith(pathSeparator)){
                basePath += parentDirectory + pathSeparator;
            } else {
                basePath += pathSeparator + parentDirectory + pathSeparator;
            }
        } else {
            basePath += pathSeparator;
        }
        File directory = new File(basePath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new BusinessException("Failed to create directory: " + basePath);
            }
        }
        return basePath + new File(objectName).getName();
    }
}
