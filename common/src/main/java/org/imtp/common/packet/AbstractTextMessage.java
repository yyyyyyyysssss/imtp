package org.imtp.common.packet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.Packet;

import java.nio.charset.StandardCharsets;

/**
 * @Description 文本聊天消息抽象类
 * @Author ys
 * @Date 2024/4/7 10:35
 */
@Getter
@Setter
public abstract class AbstractTextMessage extends Packet {

    //最大字符长度
    @JsonIgnore
    protected final int MAX_CHAR_LENGTH =2048;

    protected String text;

    //由客户端生成，应答确认消息时会带上此id给到客户端
    protected Long ackId;

    protected long timestamp;

    public AbstractTextMessage(){
        super();
    }

    public AbstractTextMessage(ByteBuf byteBuf, Header header){
        super(header);
        int textLength = byteBuf.readInt();
        byte[] bytes = new byte[textLength];
        byteBuf.readBytes(bytes);
        this.text = new String(bytes, StandardCharsets.UTF_8);
        this.ackId = byteBuf.readLong();
        this.timestamp = byteBuf.readLong();
    }

    public AbstractTextMessage(String message, long sender, long receiver, Command command,Long ackId, boolean groupFlag) {
        super(sender, receiver, command,groupFlag);
        if(StringUtil.isNullOrEmpty(message) || StringUtil.length(message) > MAX_CHAR_LENGTH){
            throw new RuntimeException("messages cannot be empty or exceed the maximum length limit");
        }
        this.text = message;
        this.ackId = ackId;
    }

    @Override
    public void encodeBodyAsByteBuf(ByteBuf byteBuf) {
        byte[] bytes = this.text.getBytes(StandardCharsets.UTF_8);
        //文本消息长度
        byteBuf.writeInt(bytes.length);
        //文本消息内容
        byteBuf.writeBytes(bytes);
        //确认id
        byteBuf.writeLong(this.ackId);
        //时间戳
        byteBuf.writeLong(timestamp);
        encodeBodyAsByteBuf0(byteBuf);
    }

    public abstract void encodeBodyAsByteBuf0(ByteBuf byteBuf);

    //20 = 4字节内容长度+8字节应答id+8字节服务器时间戳
    @JsonIgnore
    @Override
    public int getBodyLength() {
        return this.text.getBytes(StandardCharsets.UTF_8).length + 12 + 8;
    }

    @JsonIgnore
    public String getMessage() {
        return text;
    }

    @JsonIgnore
    public int getMAX_CHAR_LENGTH() {
        return MAX_CHAR_LENGTH;
    }

    @JsonIgnore
    public AbstractTextMessage additionTimestamp(){
        this.timestamp = System.currentTimeMillis();
        return this;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
