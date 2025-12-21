package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public abstract class AbstractStringMessage extends AbstractMessage{

    protected String content;

    protected AbstractStringMessage() {
    }

    protected AbstractStringMessage(String content) {
        this.content = content;
    }

    @Override
    protected final void encodeBody0(ByteBuf byteBuf) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    @Override
    protected final void decodeBody0(ByteBuf byteBuf) {
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        this.content = new String(bytes, StandardCharsets.UTF_8);
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
