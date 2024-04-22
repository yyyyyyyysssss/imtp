package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;
import lombok.Getter;
import org.imtp.common.enums.Command;
import java.nio.charset.StandardCharsets;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/7 10:35
 */
@Getter
public class TextMessage extends Packet{

    //最大字符长度
    protected final int MAX_CHAR_LENGTH = 1000;

    protected String message;

    public TextMessage(ByteBuf byteBuf,Header header){
        super(header);
        byte[] bytes = new byte[header.getLength()];
        byteBuf.readBytes(bytes);
        this.message = new String(bytes, StandardCharsets.UTF_8);
    }

    public TextMessage(String message,long sender, long receiver, Command command) {
        super(sender, receiver, command, message.getBytes(StandardCharsets.UTF_8).length);
        if(StringUtil.isNullOrEmpty(message) || StringUtil.length(message) > MAX_CHAR_LENGTH){
            throw new RuntimeException("messages cannot be empty or exceed the maximum length limit");
        }
        this.message = message;
    }

    @Override
    public void encodeBodyAsByteBuf(ByteBuf byteBuf) {
        byteBuf.writeBytes(this.message.getBytes(StandardCharsets.UTF_8));
    }



}
