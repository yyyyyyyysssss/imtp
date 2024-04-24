package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.Command;
import org.imtp.common.utils.CRC16Utils;

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

    public Packet(long sender, long receiver, Command command, int bodyLength){
        this(new Header(sender,receiver,command,bodyLength));
    }

    public Packet(Header header){
        this.header = header;
    }

    public void encodeAsByteBuf(ByteBuf byteBuf) {
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
        this.verify = CRC16Utils.calculateCRC(data);
        //写入数据校验位
        byteBuf.writeShort(verify);
    }

    public abstract void encodeBodyAsByteBuf(ByteBuf byteBuf);

    public Long getSender(){

        return this.header.getSender();
    }

    public Long getReceiver(){

        return this.header.getReceiver();
    }

}
