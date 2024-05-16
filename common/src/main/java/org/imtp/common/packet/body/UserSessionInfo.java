package org.imtp.common.packet.body;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/16 16:12
 */
@Getter
@Setter
public class UserSessionInfo {

    private Long id;

    private Long userId;

    private String name;

    private Long receiverUserId;

    private String avatar;

    private Integer lastMsgType;

    private String lastMsgContent;

    private Long lastMsgTime;

}
