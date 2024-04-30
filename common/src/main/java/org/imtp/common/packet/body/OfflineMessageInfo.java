package org.imtp.common.packet.body;

import lombok.*;

import java.util.Date;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/30 13:57
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfflineMessageInfo {

    private Long id;

    private Long sender;

    private Long receiver;

    private Integer type;

    private String content;

    private Long sendTime;

    @Override
    public String toString() {
        return "OfflineMessageInfo{" +
                "id=" + id +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", sendTime=" + sendTime +
                '}';
    }
}
