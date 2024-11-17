package org.imtp.web.domain.dto;

import lombok.Getter;
import lombok.Setter;
import org.imtp.web.domain.entity.FileUpload;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/18 21:01
 */
@Getter
@Setter
public class FileInfoDTO{

    //文件名称
    private String filename;

    private String fileType;

    //文件总大小
    private Long totalSize;

    //总块数
    private Integer totalChunk;

    //每块的大小(最后一块文件大小小于等于该值)
    private Integer chunkSize;

}
