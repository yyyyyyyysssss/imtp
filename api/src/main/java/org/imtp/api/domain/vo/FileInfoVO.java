package org.imtp.api.domain.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2025/8/1 17:46
 */
@Getter
@Setter
public class FileInfoVO {

    //文件名称
    private String filename;

    //文件类型
    private String fileType;

    //文件总大小
    private Long totalSize;

}
