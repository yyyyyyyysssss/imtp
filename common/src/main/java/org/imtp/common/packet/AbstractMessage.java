package org.imtp.common.packet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.imtp.common.enums.MessageTypeV2;
import org.imtp.common.utils.JsonUtil;

import java.nio.charset.StandardCharsets;


public abstract class AbstractMessage {

    protected long timestamp;

    private MessageMetadata contentMetadata;

    protected AbstractMessage() {

    }

    protected AbstractMessage(MessageMetadata contentMetadata) {
        this.contentMetadata = contentMetadata;
    }

    public final byte[] encodeBody() {
        ByteBuf byteBuf = Unpooled.buffer();
        // 时间戳
        byteBuf.writeLong(timestamp);
        if (this.contentMetadata == null) {
            byteBuf.writeInt(0);
        } else {
            byte[] messageMetadataBytes = JsonUtil.toJSONString(this.contentMetadata).getBytes(StandardCharsets.UTF_8);
            byteBuf.writeInt(messageMetadataBytes.length);
            byteBuf.writeBytes(messageMetadataBytes);
        }
        // 子类字段
        encodeBody0(byteBuf);
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    public final void decodeBody(ByteBuf byteBuf) {
        // 时间戳
        this.timestamp = byteBuf.readLong();
        // 消息元信息
        int messageMetadataLength = byteBuf.readInt();
        if (messageMetadataLength > 0) {
            byte[] messageMetadataBytes = new byte[messageMetadataLength];
            byteBuf.readBytes(messageMetadataBytes);
            this.contentMetadata = JsonUtil.parseObject(messageMetadataBytes, MessageMetadata.class);
        }
        // 子类字段
        decodeBody0(byteBuf);
    }

    public final int getBodyLength() {
        int contentMetadataLength = 0;
        if (this.contentMetadata != null) {
            contentMetadataLength = JsonUtil.toJSONString(this.contentMetadata).getBytes(StandardCharsets.UTF_8).length;
        }
        return 8 // timestamp
                + 4 // metadata length
                + contentMetadataLength
                + getBodyLength0();
    }

    public abstract MessageTypeV2 getMessageType();

    protected abstract void encodeBody0(ByteBuf byteBuf);

    protected abstract void decodeBody0(ByteBuf byteBuf);

    protected abstract int getBodyLength0();

    @JsonIgnore
    public AbstractMessage additionTimestamp() {
        this.timestamp = System.currentTimeMillis();
        return this;
    }
}
