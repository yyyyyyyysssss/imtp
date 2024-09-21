package org.imtp.common.packet.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/21 22:44
 */
@Getter
@Setter
public class OfflineMessageDTO {

    public OfflineMessageDTO(){}

    public OfflineMessageDTO(Long msgId,Long userId){
        this.msgId = msgId;
        this.userId = userId;
    }

    private Long msgId;

    private Long userId;

}
