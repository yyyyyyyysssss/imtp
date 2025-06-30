package org.imtp.api.service;

import org.imtp.api.domain.dto.FileInfoDTO;
import org.imtp.api.domain.dto.FileChunkDTO;
import org.imtp.api.domain.vo.FileUploadProgressVO;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

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

}
