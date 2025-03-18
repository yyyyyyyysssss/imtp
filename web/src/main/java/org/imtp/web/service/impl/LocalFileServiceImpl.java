package org.imtp.web.service.impl;

import groovy.lang.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.imtp.web.config.exception.BusinessException;
import org.imtp.web.enums.FileStorageType;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    private final int bufferSize = 4096;


    @Override
    public String uploadId(String filename,String fileType) {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    @Override
    public FileStorageType fileStorageType() {
        return FileStorageType.LOCAL;
    }

    @Override
    public String storePart(String uploadId, InputStream inputStream, String filename, Long chunkSize, Integer chunkIndex, Long partSize) {
        String tmpFilePath = tmpdir + uploadId + ".tmp";
        try (RandomAccessFile raf = new RandomAccessFile(tmpFilePath, "rw")) {
            raf.seek(chunkIndex * chunkSize);
            byte[] buffer = new byte[bufferSize];
            int n;
            while ((n = inputStream.read(buffer)) != -1) {
                raf.write(buffer, 0, n);
            }
        } catch (IOException e) {
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
        return null;
    }

    @Override
    public Tuple2<String, String> mergePart(String uploadId, String filename, Integer totalChunk) {
        String tmpFilePath = tmpdir + uploadId + ".tmp";
        Path tmpPath = Paths.get(tmpFilePath);
        String newFilePath = tmpdir + filename;
        Path path = Paths.get(newFilePath);
        try {
            Files.move(tmpPath, path, StandardCopyOption.REPLACE_EXISTING);
            log.info("upload success; filename:{}, accessUrl:{}", filename, newFilePath);
            return new Tuple2<>(null,newFilePath);
        } catch (IOException e) {
            log.error("upload  Files.move error: ", e);
            throw new BusinessException(e);
        }
    }

    @Override
    public String temporaryUrl(String uploadId) {
        throw new UnsupportedOperationException("本地文件暂不支持生成临时访问url");
    }

    @Override
    public Tuple2<String, String> simpleUpload(InputStream inputStream, String filename, String contentType, Long size) {
        String newFilePath = tmpdir + filename;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(newFilePath);
            byte[] buffer = new byte[bufferSize];
            int n;
            while ((n = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, n);
            }
            return new Tuple2<>(null,newFilePath);
        }catch (Exception e){
            log.error("simpleUpload error: ",e);
            throw new BusinessException("simpleUpload error: " + e.getMessage());
        }finally {
            if (fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    log.error("simpleUpload error: ",e);
                }
            }
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("simpleUpload error: ",e);
                }
            }
        }
    }
}
