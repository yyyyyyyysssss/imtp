package org.imtp.common.packet.body;

import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;
import org.imtp.common.packet.MessageMetadata;

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

    private MessageType lastMsgType;

    private DeliveryMethod deliveryMethod;

    private String lastMsgContent;

    private Long lastMsgTime;

    private Long lastSendMsgUserId;

    private String lastUserAvatar;

    private String lastUserName;

    private MessageMetadata lastMessageMetadata;

}
