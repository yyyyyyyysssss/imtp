package org.imtp.packet;

import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

/**
 * @Description
 * @Author ys
 * @Date 2023/4/2 13:33
 */
@Getter
@Setter
@Builder
public class Header{
    //魔数
    private short magic;
    //协议版本
    private byte ver;
    //序列化类型
    private byte serializeType;
    //包的序列号
    private long seq;
    //业务指令
    private byte cmd;
    //消息长度
    private int length;

    @Tolerate
    public Header(){}

    public void encodeAsByteBuf(ByteBuf byteBuf) {
        byteBuf.writeShort(this.magic);
        byteBuf.writeByte(this.ver);
        byteBuf.writeByte(this.serializeType);
        byteBuf.writeLong(this.seq);
        byteBuf.writeByte(this.cmd);
        byteBuf.writeInt(this.length);
    }
}
