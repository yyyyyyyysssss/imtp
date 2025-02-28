package org.imtp.common.packet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.Packet;

import java.nio.charset.StandardCharsets;

/**
 * @Description
 * @Author ys
 * @Date 2025/2/25 16:54
 */

public abstract class AbstractSignalingMessage extends Packet {

    protected String content;

    public AbstractSignalingMessage(){
        super();
    }

    public AbstractSignalingMessage(String content,long sender, long receiver, Command command, boolean groupFlag) {
        super(sender, receiver, command,groupFlag);
        this.content = content;
    }

    public AbstractSignalingMessage(ByteBuf byteBuf, Header header){
        super(header);
        int textLength = byteBuf.readInt();
        if (textLength == 0){
            this.content = null;
        }else {
            byte[] bytes = new byte[textLength];
            byteBuf.readBytes(bytes);
            this.content = new String(bytes, StandardCharsets.UTF_8);
        }
    }

    @Override
    public void encodeBodyAsByteBuf(ByteBuf byteBuf) {
        if(this.content == null || this.content.isEmpty()){
            byteBuf.writeInt(0);
            encodeBodyAsByteBuf0(byteBuf);
            return;
        }
        byte[] bytes = this.content.getBytes(StandardCharsets.UTF_8);
        //文本消息长度
        byteBuf.writeInt(bytes.length);
        //文本消息内容
        byteBuf.writeBytes(bytes);
        encodeBodyAsByteBuf0(byteBuf);
    }

    public abstract void encodeBodyAsByteBuf0(ByteBuf byteBuf);

    @JsonIgnore
    @Override
    public int getBodyLength() {
        int contentLength = this.content == null || this.content.isEmpty() ? 0 : this.content.getBytes(StandardCharsets.UTF_8).length;
        return contentLength + 4 + getBodyLength0();
    }

    @JsonIgnore
    public int getBodyLength0(){

        return 0;
    }

    public String getContent() {
        return content;
    }
}
