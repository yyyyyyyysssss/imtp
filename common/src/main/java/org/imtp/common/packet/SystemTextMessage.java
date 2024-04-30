package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.Packet;

import java.nio.charset.StandardCharsets;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/29 16:23
 */
public abstract class SystemTextMessage extends Packet {

    protected String text;

    public SystemTextMessage(long sender, long receiver, Command command) {
        super(sender, receiver, command);
    }

    public SystemTextMessage(ByteBuf byteBuf, Header header) {
        super(header);
        if(header.getLength() == 0){
            return;
        }
        byte[] bytes = new byte[header.getLength()];
        byteBuf.readBytes(bytes);
        this.text = new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void encodeBodyAsByteBuf(ByteBuf byteBuf) {
        if(text != null){
            byteBuf.writeBytes(this.text.getBytes(StandardCharsets.UTF_8));
        }
        encodeBodyAsByteBuf0(byteBuf);
    }

    protected abstract void encodeBodyAsByteBuf0(ByteBuf byteBuf);

    @Override
    public int getBodyLength() {
        return text == null ? 0 : this.text.getBytes(StandardCharsets.UTF_8).length;
    }
}
