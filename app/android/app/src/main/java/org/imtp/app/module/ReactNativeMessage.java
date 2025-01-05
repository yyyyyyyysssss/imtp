package org.imtp.app.module;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;
import org.imtp.common.packet.MessageMetadata;
import org.imtp.common.packet.base.Packet;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReactNativeMessage {

    private Long ackId;

    private Long sessionId;

    private MessageType type;

    private Long sender;

    private Long receiver;

    private DeliveryMethod deliveryMethod;

    private String content;

    private MessageMetadata contentMetadata;

    public Long getAckId() {
        return ackId;
    }

    public void setAckId(Long ackId) {
        this.ackId = ackId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public Long getReceiver() {
        return receiver;
    }

    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }

    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageMetadata getContentMetadata() {
        return contentMetadata;
    }

    public void setContentMetadata(MessageMetadata contentMetadata) {
        this.contentMetadata = contentMetadata;
    }
}
