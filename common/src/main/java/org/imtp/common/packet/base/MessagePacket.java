package org.imtp.common.packet.base;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.enums.MessageTypeV2;
import org.imtp.common.packet.AbstractMessage;

public class MessagePacket extends Packet {

    private MessageTypeV2 messageType;

    private Long ackId;

    private AbstractMessage message;

    public MessagePacket() {

    }

    public MessagePacket(AbstractMessage message, long sender, long receiver, Long ackId, boolean groupFlag) {
        super(sender, receiver, Command.MESSAGE, groupFlag);
        this.message = message;
        this.messageType = message.getMessageType();
        this.ackId = ackId;
    }


    @Override
    public void encodeBodyAsByteBuf(ByteBuf byteBuf) {
        // 消息类型
        byteBuf.writeByte(messageType.getValue());
        // 确认id
        byteBuf.writeLong(ackId);
        // 具体业务消息
        byte[] bytes = message.encodeBody();
        // 消息长度
        byteBuf.writeInt(bytes.length);
        // 消息内容
        byteBuf.writeBytes(bytes);
    }

    @Override
    public int getBodyLength() {
        return 1    // messageType
                + 8 // ackId
                + 4 // msg length
                + message.getBodyLength();
    }

    public void decodeBodyAsByteBuf(ByteBuf byteBuf) {
        // 消息类型
        this.messageType = MessageTypeV2.findByValue(byteBuf.readByte());
        // 确认id
        this.ackId = byteBuf.readLong();
        // 3. message body
        int bodyLen = byteBuf.readInt();
        // 限定子消息的读取边界
        ByteBuf bodyBuf = byteBuf.readSlice(bodyLen);
        // 创建消息
        this.message = messageType.create();
        // 由子类填充自身
        this.message.decodeBody(bodyBuf);
    }


    public MessageTypeV2 getMessageType() {
        return messageType;
    }

    public AbstractMessage getMessage() {
        return message;
    }
}
