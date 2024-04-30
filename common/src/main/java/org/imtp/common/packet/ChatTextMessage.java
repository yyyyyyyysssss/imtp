package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.Packet;

import java.nio.charset.StandardCharsets;

/**
 * @Description 文本聊天消息抽象类
 * @Author ys
 * @Date 2024/4/7 10:35
 */
public abstract class ChatTextMessage extends Packet {

    //最大字符长度
    protected final int MAX_CHAR_LENGTH =2048;

    protected String text;

    public ChatTextMessage(ByteBuf byteBuf, Header header){
        super(header);
        byte[] bytes = new byte[header.getLength()];
        byteBuf.readBytes(bytes);
        this.text = new String(bytes, StandardCharsets.UTF_8);
    }

    public ChatTextMessage(String message, long sender, long receiver, Command command,boolean groupFlag) {
        super(sender, receiver, command,groupFlag);
        if(StringUtil.isNullOrEmpty(message) || StringUtil.length(message) > MAX_CHAR_LENGTH){
            throw new RuntimeException("messages cannot be empty or exceed the maximum length limit");
        }
        this.text = message;
    }

    public ChatTextMessage(String message, long sender, long receiver, Command command) {
        super(sender, receiver, command);
        if(StringUtil.isNullOrEmpty(message) || StringUtil.length(message) > MAX_CHAR_LENGTH){
            throw new RuntimeException("messages cannot be empty or exceed the maximum length limit");
        }
        this.text = message;
    }

    @Override
    public void encodeBodyAsByteBuf(ByteBuf byteBuf) {
        byteBuf.writeBytes(this.text.getBytes(StandardCharsets.UTF_8));
        encodeBodyAsByteBuf0(byteBuf);
    }

    public abstract void encodeBodyAsByteBuf0(ByteBuf byteBuf);

    @Override
    public int getBodyLength() {
        return this.text.getBytes(StandardCharsets.UTF_8).length;
    }

    public String getMessage() {
        return text;
    }
}
