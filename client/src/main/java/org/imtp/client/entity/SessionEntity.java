package org.imtp.client.entity;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;
import org.imtp.common.packet.MessageMetadata;

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

    private String lastUserName;

    private StringProperty count;

    private MessageMetadata lastMessageMetadata;

    public StringProperty countProperty(){
        if (count == null){
            count = new SimpleStringProperty(this,"count");
        }
        return count;
    }

    public String getCount() {
        return countProperty().get();
    }

    public void setCount(String count) {
        this.countProperty().set(count);
    }

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
                ", lastUserName='" + lastUserName + '\'' +
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
