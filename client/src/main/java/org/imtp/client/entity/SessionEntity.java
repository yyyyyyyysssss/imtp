package org.imtp.client.entity;

import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.MessageType;

@Getter
@Setter
public class SessionEntity {

    private Long id;

    private String name;

    private String avatar;

    private Long receiverId;

    private MessageType lastMsgType;

    private String lastMsg;

    private Long timestamp;

    @Override
    public String toString() {
        return "SessionEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", receiverId=" + receiverId +
                ", lastMsgType=" + lastMsgType +
                ", lastMsg='" + lastMsg + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
