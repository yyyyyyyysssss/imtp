package org.imtp.common.packet.base;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.MessageTypeV2;
import org.imtp.common.packet.MessageMetadata;

import java.nio.charset.StandardCharsets;

public class MessagePacket extends AbstractMessagePacket {

    protected String content;

    public MessagePacket(){
        super();
    }

    public MessagePacket(Header header){
        super(header);
    }

    public MessagePacket(
            String content,
            MessageTypeV2 messageType,
            MessageMetadata contentMetadata,
            long sender,
            long receiver,
            Long ackId,
            boolean groupFlag) {
        super(messageType, contentMetadata, sender, receiver, ackId, groupFlag);
        this.content = content;
    }

    @Override
    protected final void encodeBody0(ByteBuf byteBuf) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    @Override
    protected final void decodeBody0(ByteBuf byteBuf,AbstractMessagePacket abstractMessagePacket) {
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        MessagePacket messagePacket = (MessagePacket)abstractMessagePacket;
        messagePacket.content = new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    protected final int getBodyLength0() {
        return 4 // text length
                + content.getBytes(StandardCharsets.UTF_8).length;
    }

    public String getContent() {
        return content;
    }
}
