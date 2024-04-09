package org.imtp.packet;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import org.imtp.enums.Command;
import org.imtp.enums.ProtocolVersion;

/**
 * @Description 协议头
 * @Author ys
 * @Date 2023/4/2 13:33
 */
@Getter
public class Header{
    //魔数 占2个字节
    private short magic = 0xdf;
    //协议版本 占1个字节
    private ProtocolVersion ver;
    //发送端标识 占8个字节
    private long sender;
    //接收端标识 占8个字节
    private long receiver;
    //业务指令 占1个字节
    private Command cmd;
    //消息长度 占4个字节
    private int length;

    public Header(long sender, long receiver, Command cmd, int length){
        this(ProtocolVersion.VER1,sender,receiver,cmd,length);
    }

    public Header(ByteBuf byteBuf){
        this.magic = byteBuf.readShort();
        if(magic != 0xdf){
            throw new RuntimeException("invalid magic number: " + magic);
        }

        byte v = byteBuf.readByte();
        this.ver = ProtocolVersion.find(v);
        if(ver == null){
            throw new RuntimeException("Unknown protocol version: " + v);
        }

        this.sender = byteBuf.readLong();
        this.receiver = byteBuf.readLong();

        byte c = byteBuf.readByte();
        this.cmd = Command.find(c);
        if(cmd == null){
            throw new UnsupportedOperationException("Unsupported operation command" + c);
        }

        this.length = byteBuf.readInt();
    }

    public Header(ProtocolVersion ver,long sender,long receiver,Command cmd,int length){
        this.ver = ver;
        this.sender = sender;
        this.receiver = receiver;
        this.cmd = cmd;
        this.length = length;
    }

    public void encodeAsByteBuf(ByteBuf byteBuf) {
        if(this.magic != 0xdf){
            throw new RuntimeException("magic error");
        }
        byteBuf.writeShort(this.magic);
        byteBuf.writeByte(this.ver.getVer());
        byteBuf.writeLong(this.sender);
        byteBuf.writeLong(this.receiver);
        byteBuf.writeByte(this.cmd.getCmdCode());
        byteBuf.writeInt(this.length);
    }

    public static int headLength(){

        return 24;
    }

}
