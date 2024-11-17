package org.imtp.web.service;

import org.imtp.web.domain.dto.FileInfoDTO;
import org.imtp.web.domain.dto.FileChunkDTO;
import org.imtp.web.domain.vo.FileUploadProgressVO;
import org.springframework.web.multipart.MultipartFile;

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

    String temporaryUrl(String uploadId);

    String simpleUpload(MultipartFile file);

}
