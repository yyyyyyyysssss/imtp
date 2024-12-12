package org.imtp.common.packet.common;

import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.packet.AbstractTextMessage;
import org.imtp.common.packet.MessageMetadata;
import org.imtp.common.packet.base.Packet;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/21 22:37
 */
@Getter
@Setter
public class MessageDTO {

    public MessageDTO(){}

    public MessageDTO(Packet packet){
        this.senderUserId = packet.getSender();
        this.receiverUserId = packet.getReceiver();
        this.type = (int) packet.getHeader().getCmd().getCmdCode();
        if(packet.isGroup()){
            this.deliveryMethod = DeliveryMethod.GROUP;
        }else {
            this.deliveryMethod = DeliveryMethod.SINGLE;
        }
        if (packet instanceof AbstractTextMessage abstractTextMessage){
            this.sessionId = abstractTextMessage.getSessionId();
            this.contentMetadata = abstractTextMessage.getContentMetadata();
        }
    }

    private Long id;

    private Long sessionId;

    private Long senderUserId;

    private Long receiverUserId;

    private Integer type;

    private String content;

    private MessageMetadata contentMetadata;

    private DeliveryMethod deliveryMethod;

}
