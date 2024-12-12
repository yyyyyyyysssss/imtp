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
import org.imtp.common.utils.JsonUtil;

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
    protected final int MAX_CHAR_LENGTH = 2048;

    protected String text;

    //由客户端生成，应答确认消息时会带上此id给到客户端
    protected Long ackId;

    protected long timestamp;

    protected Long sessionId;

    //消息元信息
    private MessageMetadata contentMetadata;

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
        this.sessionId = byteBuf.readLong();
        int messageMetadataLength = byteBuf.readInt();
        if (messageMetadataLength > 0){
            byte[] messageMetadataBytes = new byte[messageMetadataLength];
            byteBuf.readBytes(messageMetadataBytes);
            this.contentMetadata = JsonUtil.parseObject(messageMetadataBytes,MessageMetadata.class);
        }
    }

    public AbstractTextMessage(String message,MessageMetadata messageMetadata,long sessionId, long sender, long receiver, Command command,Long ackId, boolean groupFlag) {
        super(sender, receiver, command,groupFlag);
        if(StringUtil.isNullOrEmpty(message) || StringUtil.length(message) > MAX_CHAR_LENGTH){
            throw new RuntimeException("messages cannot be empty or exceed the maximum length limit");
        }
        this.text = message;
        this.ackId = ackId;
        this.contentMetadata = messageMetadata;
        this.sessionId = sessionId;
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
        //会话id
        byteBuf.writeLong(sessionId);
        //消息元信息长度
        if (this.contentMetadata == null){
            byteBuf.writeInt(0);
        }else {
            byte[] messageMetadataBytes = JsonUtil.toJSONString(this.contentMetadata).getBytes(StandardCharsets.UTF_8);
            byteBuf.writeInt(messageMetadataBytes.length);
            byteBuf.writeBytes(messageMetadataBytes);
        }
        encodeBodyAsByteBuf0(byteBuf);
    }

    public abstract void encodeBodyAsByteBuf0(ByteBuf byteBuf);

    //20 = 4字节内容长度+8字节应答id+8字节服务器时间戳 + 8字节会话id +4字节消息元信息长度
    @JsonIgnore
    @Override
    public int getBodyLength() {
        int contentMetadataLength = 0;
        if (this.contentMetadata != null){
            contentMetadataLength = JsonUtil.toJSONString(this.contentMetadata).getBytes(StandardCharsets.UTF_8).length;
        }
        return this.text.getBytes(StandardCharsets.UTF_8).length + 4 + 8 + 8 + 8 + 4 + contentMetadataLength + getBodyLength0();
    }

    @JsonIgnore
    public int getBodyLength0(){

        return 0;
    }

    @JsonIgnore
    public String getMessage() {
        return text;
    }

    @JsonIgnore
    public AbstractTextMessage additionTimestamp(){
        this.timestamp = System.currentTimeMillis();
        return this;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getAckId() {
        return ackId;
    }

    public MessageMetadata getContentMetadata() {
        return contentMetadata;
    }

    public Long getSessionId() {
        return sessionId;
    }

    @JsonIgnore
    public int getMAX_CHAR_LENGTH() {
        return MAX_CHAR_LENGTH;
    }
}
