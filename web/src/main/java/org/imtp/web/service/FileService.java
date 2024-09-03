package org.imtp.web.service;

import org.imtp.web.domain.dto.UploadChunkDTO;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/19 10:11
 */
public interface FileService {

    String uploadId(String filename,Long totalSize);

    boolean upload(UploadChunkDTO uploadChunkDTO);

    String accessUrl(String uploadId);

}
