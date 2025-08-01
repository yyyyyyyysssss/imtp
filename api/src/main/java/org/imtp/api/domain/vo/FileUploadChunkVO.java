package org.imtp.api.domain.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2025/8/1 17:49
 */
@Getter
@Setter
public class FileUploadChunkVO {

    private String uploadId;

    //当前块索引
    private Integer chunkIndex;

    private Long uploadSize;

    private String etag;

}
