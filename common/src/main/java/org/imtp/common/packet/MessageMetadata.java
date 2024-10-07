package org.imtp.common.packet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description 用于记录消息的元信息
 * @Author ys
 * @Date 2024/9/23 13:45
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageMetadata {

    private Double width;

    private Double height;

    private String mediaType;

    private String previewUrl;

    private Long duration;

    private String durationDesc;

    private Long size;

    private String sizeDesc;

    private String name;

    private String thumbnailUrl;


    @Override
    public String toString() {
        return "MessageMetadata{" +
                "width=" + width +
                ", height=" + height +
                ", mediaType='" + mediaType + '\'' +
                ", previewUrl='" + previewUrl + '\'' +
                ", duration=" + duration +
                ", durationDesc='" + durationDesc + '\'' +
                ", size=" + size +
                ", sizeDesc='" + sizeDesc + '\'' +
                ", name='" + name + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                '}';
    }
}
