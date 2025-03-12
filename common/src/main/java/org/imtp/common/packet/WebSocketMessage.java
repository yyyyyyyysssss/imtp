package org.imtp.common.packet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/20 21:19
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebSocketMessage {

    private Long ackId;

    private MessageType type;

    private Long sender;

    private Long receiver;

    private DeliveryMethod deliveryMethod;

    private String content;

    private MessageMetadata contentMetadata;

    @Override
    public String toString() {
        return "WebSocketMessage{" +
                "ackId=" + ackId +
                ", type=" + type +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", deliveryMethod=" + deliveryMethod +
                ", content='" + content + '\'' +
                ", messageMetadata=" + contentMetadata +
                '}';
    }
}
