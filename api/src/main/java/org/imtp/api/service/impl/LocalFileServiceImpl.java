package org.imtp.api.service.impl;

import groovy.lang.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.domain.dto.FileRangeDTO;
import org.imtp.api.enums.FileStorageType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.List;
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
    public String uploadId(String filename, String fileType) {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Override
    public FileStorageType fileStorageType() {
        return FileStorageType.LOCAL;
    }

    @Override
    public String storePart(String uploadId, InputStream inputStream, String filename, Long chunkSize, Integer chunkIndex, Long partSize) {
        String tmpFilePath = newFilePath(uploadId) + ".tmp";
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
        String tmpFilePath = newFilePath(uploadId) + ".tmp";
        Path tmpPath = Paths.get(tmpFilePath);
        String newFilePath = newFilePath(filename);
        Path path = Paths.get(newFilePath);
        try {
            Files.move(tmpPath, path, StandardCopyOption.REPLACE_EXISTING);
            log.info("upload success; filename:{}, accessUrl:{}", filename, newFilePath);
            return new Tuple2<>(null, newFilePath);
        } catch (IOException e) {
            log.error("upload  Files.move error: ", e);
            throw new BusinessException(e);
        }
    }

    @Override
    public String temporaryUrl(String uploadId, Duration duration) {
        throw new UnsupportedOperationException("本地文件暂不支持生成临时访问url");
    }

    @Override
    public Tuple2<StreamingResponseBody, Map<String, String>> getFileStream(String bucketName, String objectName, List<FileRangeDTO> rangeList) {
        String newFilePath = tmpdir + bucketName + File.separator + objectName;
        File file = new File(newFilePath);
        StreamingResponseBody responseBody = outputStream -> {
            if (rangeList != null && !rangeList.isEmpty()) {
                try (RandomAccessFile randomAccessFile = new RandomAccessFile(newFilePath, "r")) {
                    for (FileRangeDTO range : rangeList) {
                        long start = range.getStart();
                        long length;
                        // 如果 range.getEnd() 为 -1，表示下载到文件末尾
                        if (range.getEnd() == -1) {
                            length = file.length() - range.getStart();
                        } else {
                            length = range.getEnd() - range.getStart();
                        }
                        randomAccessFile.seek(start);
                        byte[] buffer = new byte[bufferSize];
                        int bytesRead;
                        while (length > 0 && (bytesRead = randomAccessFile.read(buffer, 0, (int) Math.min(bufferSize, length))) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                            length -= bytesRead;
                        }
                    }
                } catch (IOException e) {
                    log.error("getFileStream error: ", e);
                    throw new BusinessException("getFileStream error: " + e.getMessage());
                }
            } else {
                streamFile(new FileInputStream(newFilePath), outputStream);
            }
        };
        try {
            return new Tuple2<>(responseBody, Map.of("Content-Type", Files.probeContentType(Paths.get(newFilePath))));
        } catch (Exception e) {
            log.error("getFileStream error: ", e);
            throw new BusinessException("getFileStream error: " + e.getMessage());
        }
    }

    @Override
    public Tuple2<String, String> simpleUpload(InputStream inputStream, String filename, String contentType, Long size) {
        String newFilePath = newFilePath(filename);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(newFilePath);
            byte[] buffer = new byte[bufferSize];
            int n;
            while ((n = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, n);
            }
            return new Tuple2<>(null, newFilePath);
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

    private String newFilePath(String filename) {
        String basePath = tmpdir + bucketName + File.separator;
        File directory = new File(basePath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new BusinessException("Failed to create directory: " + basePath);
            }
        }
        return basePath + filename;
    }

    @Override
    public String pathSeparator() {
        return File.separator;
    }
}
