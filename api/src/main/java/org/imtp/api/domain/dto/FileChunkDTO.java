package org.imtp.api.domain.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/18 21:01
 */
@Getter
@Setter
public class FileChunkDTO {

    //id
    private String uploadId;

    //每块的大小(最后一块文件大小小于等于该值)
    private Long chunkSize;

    //当前块索引
    private Integer chunkIndex;

    //文件
    private MultipartFile file;

}
