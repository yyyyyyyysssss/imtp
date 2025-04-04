package org.imtp.common.packet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.Packet;

import java.nio.charset.StandardCharsets;

/**
 * @Description 系统消息抽象类
 * @Author ys
 * @Date 2024/4/29 16:23
 */
public abstract class AbstractSystemMessage extends Packet {

    protected String text;

    public AbstractSystemMessage(long sender, long receiver, Command command) {
        super(sender, receiver, command);
    }

    public AbstractSystemMessage(ByteBuf byteBuf, Header header) {
        super(header);
        if(header.getLength() == 0){
            return;
        }
        int textLength = header.getLength() - getBodyLength0();
        byte[] bytes = new byte[textLength];
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

    @Override
    @JsonIgnore
    public int getBodyLength() {
        return text == null ? getBodyLength0() : this.text.getBytes(StandardCharsets.UTF_8).length + getBodyLength0();
    }

    public abstract void encodeBodyAsByteBuf0(ByteBuf byteBuf);

    @JsonIgnore
    public abstract int getBodyLength0();
}
