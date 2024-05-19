package org.imtp.client.entity;

import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;

@Getter
@Setter
public class SessionEntity {

    private Long id;

    private String name;

    private String avatar;

    private Long receiverUserId;

    private MessageType lastMsgType;

    private DeliveryMethod deliveryMethod;

    private String lastMsg;

    private Long timestamp;

    @Override
    public String toString() {
        return "SessionEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", receiverUserId=" + receiverUserId +
                ", lastMsgType=" + lastMsgType +
                ", deliveryMethod=" + deliveryMethod +
                ", lastMsg='" + lastMsg + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
