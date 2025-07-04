package org.imtp.api.service;

import groovy.lang.Tuple2;
import org.imtp.api.domain.dto.FileInfoDTO;
import org.imtp.api.domain.dto.FileChunkDTO;
import org.imtp.api.domain.dto.FileRangeDTO;
import org.imtp.api.domain.vo.FileUploadProgressVO;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/19 10:11
 */
public interface FileService {

    String uploadId(FileInfoDTO fileInfoDTO);

    boolean uploadChunk(FileChunkDTO fileChunkDTO);

    FileUploadProgressVO uploadProgress(String uploadId);

    String accessUrl(String uploadId);

    String temporaryUrl(String uploadId, Duration duration);

    String simpleUpload(MultipartFile file);

    Tuple2<StreamingResponseBody, Map<String,String>> getFileStream(String bucketName, String objectName, List<FileRangeDTO> rangeList);

    FileInfoDTO getFileInfo(String bucketName, String objectName);

}
