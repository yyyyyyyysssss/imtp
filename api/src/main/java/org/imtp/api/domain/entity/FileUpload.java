package org.imtp.api.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;
import org.imtp.api.enums.FileStorageType;
import org.imtp.api.enums.FileUploadStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/19 15:23
 */
@Getter
@Setter
@TableName("im_files")
@Builder
public class FileUpload {

    @Tolerate
    public FileUpload(){

    }

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @TableField("upload_id")
    private String uploadId;

    @TableField("file_name")
    private String fileName;

    @TableField("file_type")
    private String fileType;

    @TableField("total_size")
    private Long totalSize;

    @TableField("total_chunk")
    private Integer totalChunk;

    @TableField("chunk_size")
    private Integer chunkSize;

    @TableField("uploaded_chunk_count")
    private Integer uploadedChunkCount;

    @TableField("etag")
    private String etag;

    @TableField("access_url")
    private String accessUrl;

    @TableField("original_url")
    private String originalUrl;

    @TableField("md5")
    private String md5;

    @TableField("status")
    private FileUploadStatus status;

    @TableField("storage_type")
    private FileStorageType storageType;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

}
