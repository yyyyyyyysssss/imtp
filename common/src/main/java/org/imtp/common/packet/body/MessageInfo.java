package org.imtp.common.packet.body;

import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.packet.MessageMetadata;

/**
 * @Description
 * @Author ys
 * @Date 2024/12/12 15:30
 */
@Getter
@Setter
public class MessageInfo {

    private Long id;

    private Long sessionId;

    private Long senderUserId;

    private Long receiverUserId;

    private Integer type;

    private String content;

    private MessageMetadata contentMetadata;

    private Long sendTime;

    private DeliveryMethod deliveryMethod;

    private String name;

    private String avatar;

}
