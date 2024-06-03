package org.imtp.client.entity;

import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;

import java.util.Objects;

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

    private Long lastSendMsgUserId;

    private String lastUserAvatar;

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
                ", lastSendMsgUserId=" + lastSendMsgUserId +
                ", lastUserAvatar='" + lastUserAvatar + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        SessionEntity that = (SessionEntity) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
