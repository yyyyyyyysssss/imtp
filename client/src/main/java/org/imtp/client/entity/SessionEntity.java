package org.imtp.client.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionEntity {

    private Long id;

    private String name;

    private Long receiverId;

    private Long timestamp;

    @Override
    public String toString() {
        return "SessionEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", receiverId=" + receiverId +
                ", timestamp=" + timestamp +
                '}';
    }
}
