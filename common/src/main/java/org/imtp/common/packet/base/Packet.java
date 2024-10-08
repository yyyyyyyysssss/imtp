package org.imtp.common.packet.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.Command;
import org.imtp.common.enums.MessageType;
import org.imtp.common.utils.CRC16Util;

/**
 * @Description
 * @Author ys
 * @Date 2023/4/2 13:49
 */
@Getter
@Setter
public abstract class Packet{

    protected Header header;

    //数据校验位占2个字节
    protected short verify;

    public Packet(){}

    public Packet(long sender, long receiver, Command command,boolean groupFlag){
        this(new Header(sender,receiver,command,groupFlag));
    }

    public Packet(long sender, long receiver, Command command){
        this(new Header(sender,receiver,command,false));
    }

    public Packet(Header header){
        this.header = header;
    }

    public void encodeAsByteBuf(ByteBuf byteBuf) {
        int bodyLength = this.getBodyLength();
        this.header.setLength(bodyLength);
        //编码消息头
        this.header.encodeAsByteBuf(byteBuf);
        //编码消息体
        encodeBodyAsByteBuf(byteBuf);
        //标记读写索引
        byteBuf.markReaderIndex();
        byteBuf.skipBytes(Header.headLength());
        //读取数据进行校验
        byte[] data = new byte[this.header.getLength()];
        byteBuf.readBytes(data);
        //还原读写索引
        byteBuf.resetReaderIndex();
        //数据校验码
        this.verify = CRC16Util.calculateCRC(data);
        //写入数据校验位
        byteBuf.writeShort(verify);
    }

    public abstract void encodeBodyAsByteBuf(ByteBuf byteBuf);

    public abstract int getBodyLength();


    @JsonIgnore
    public boolean isGroup(){

        return this.header.isGroupFlag();
    }

    @JsonIgnore
    public Long getSender(){

        return this.header.getSender();
    }

    @JsonIgnore
    public Long getReceiver(){

        return this.header.getReceiver();
    }

    @JsonIgnore
    public Command getCommand(){

        return this.header.getCmd();
    }

    @JsonIgnore
    public Long realSender(){
        return this.isGroup() ? this.getReceiver() : this.getSender();
    }

    @JsonIgnore
    public MessageType messageType(){

        return MessageType.findMessageTypeByValue((int) this.header.getCmd().getCmdCode());
    }
}
