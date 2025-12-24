package org.imtp.common.packet.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.enums.MessageTypeV2;
import org.imtp.common.packet.MessageMetadata;
import org.imtp.common.utils.JsonUtil;

import java.nio.charset.StandardCharsets;


public abstract class AbstractMessagePacket extends Packet {

    protected MessageTypeV2 messageType;

    protected long ackId;

    protected long timestamp;

    protected MessageMetadata contentMetadata;

    public AbstractMessagePacket(){}

    protected AbstractMessagePacket(Header header){
        super(header);
    }

    protected AbstractMessagePacket(MessageTypeV2 messageType, MessageMetadata contentMetadata, long sender, long receiver, long ackId, boolean groupFlag) {
        super(sender, receiver, Command.MESSAGE_PACKET,groupFlag);
        this.messageType = messageType;
        this.contentMetadata = contentMetadata;
        this.ackId = ackId;
    }

    @Override
    public void encodeBodyAsByteBuf(ByteBuf byteBuf) {
        // 消息类型
        byteBuf.writeByte(messageType.getValue());
        // 确认id
        byteBuf.writeLong(ackId);
        // 时间戳
        byteBuf.writeLong(timestamp);
        // 消息元信息
        if (this.contentMetadata == null) {
            byteBuf.writeInt(0);
        } else {
            byte[] messageMetadataBytes = JsonUtil.toJSONString(this.contentMetadata).getBytes(StandardCharsets.UTF_8);
            byteBuf.writeInt(messageMetadataBytes.length);
            byteBuf.writeBytes(messageMetadataBytes);
        }
        // 子类字段
        encodeBody0(byteBuf);
    }

    public final static AbstractMessagePacket decodeBodyAsByteBuf(ByteBuf byteBuf, Header header) {
        MessageTypeV2 messageType = MessageTypeV2.findByValue(byteBuf.readByte());
        AbstractMessagePacket abstractMessagePacket = messageType.create();
        abstractMessagePacket.header = header;
        // 消息类型
        abstractMessagePacket.messageType = messageType;
        // 确认id
        abstractMessagePacket.ackId = byteBuf.readLong();
        // 时间戳
        abstractMessagePacket.timestamp = byteBuf.readLong();
        // 消息元信息
        int messageMetadataLength = byteBuf.readInt();
        if (messageMetadataLength > 0) {
            byte[] messageMetadataBytes = new byte[messageMetadataLength];
            byteBuf.readBytes(messageMetadataBytes);
            abstractMessagePacket.contentMetadata = JsonUtil.parseObject(messageMetadataBytes, MessageMetadata.class);
        }
        // 子类字段
        abstractMessagePacket.decodeBody0(byteBuf,abstractMessagePacket);
        return abstractMessagePacket;
    }

    public final int getBodyLength() {
        int contentMetadataLength = 0;
        if (this.contentMetadata != null) {
            contentMetadataLength = JsonUtil.toJSONString(this.contentMetadata).getBytes(StandardCharsets.UTF_8).length;
        }
        return 1    // messageType
                + 8 // ackId
                + 8 // timestamp
                + 4 // metadata length
                + contentMetadataLength
                + getBodyLength0();
    }

    protected abstract void encodeBody0(ByteBuf byteBuf);

    protected abstract void decodeBody0(ByteBuf byteBuf, AbstractMessagePacket abstractMessagePacket);

    protected abstract int getBodyLength0();

    @JsonIgnore
    public AbstractMessagePacket additionTimestamp() {
        this.timestamp = System.currentTimeMillis();
        return this;
    }

    public long getAckId() {
        return ackId;
    }

    public MessageTypeV2 getMessageType() {
        return messageType;
    }
}
