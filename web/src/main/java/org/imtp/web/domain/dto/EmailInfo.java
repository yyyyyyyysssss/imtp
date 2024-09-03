package org.imtp.web.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

import java.util.Arrays;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/13 9:35
 */
@Builder
@Getter
@Setter
public class EmailInfo {

    @Tolerate
    public EmailInfo(){}

    private String title;

    private String content;

    private String from;

    private String[] to;

    private String[] cc;

    @Override
    public String toString() {
        return "EmailInfo{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", from='" + from + '\'' +
                ", to=" + Arrays.toString(to) +
                ", cc=" + Arrays.toString(cc) +
                '}';
    }
}
