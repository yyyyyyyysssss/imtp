package org.imtp.web.domain.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2024/11/16 19:51
 */
@Getter
@Setter
public class FileUploadProgressVO {

    private String uploadId;

    private Integer totalChunk;

    private Integer uploadedChunkCount;

}
