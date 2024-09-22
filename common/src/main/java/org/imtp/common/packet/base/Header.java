package org.imtp.common.packet.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.Command;
import org.imtp.common.enums.ProtocolVersion;

/**
 * @Description 协议头
 * @Author ys
 * @Date 2023/4/2 13:33
 */
@Getter
@Setter
public class Header{
    //魔数 占1个字节
    private byte magic = (byte) 0xdf;
    //协议版本 占1个字节
    private ProtocolVersion ver;
    //发送端标识 占8个字节
    private long sender;
    //接收端标识 占8个字节
    private long receiver;
    //保留位 占1个字节
    private byte reserved;
    //业务指令 占1个字节
    private Command cmd;
    //消息长度 占4个字节
    private int length;

    //群聊标识位 取保留位从右往左的第0位 1:群聊 0:私聊
    @JsonIgnore
    private boolean groupFlag;

    public Header(){}

    public Header(long sender, long receiver, Command cmd,boolean groupFlag){
        this(ProtocolVersion.VER1,sender,receiver,cmd,0,groupFlag);
    }

    public Header(ProtocolVersion ver, long sender, long receiver, Command cmd, int length){
        this(ver,sender,receiver,cmd,length,true);
    }

    public Header(ProtocolVersion ver, long sender, long receiver, Command cmd, int length,boolean groupFlag){
        this.ver = ver;
        this.sender = sender;
        this.receiver = receiver;
        this.cmd = cmd;
        this.length = length;
        this.groupFlag = groupFlag;
        this.reserved = groupFlag ? setBitValue(this.reserved, 0, (byte) 1) : this.reserved;
    }

    public Header(ByteBuf byteBuf){
        this.magic = byteBuf.readByte();
        if(magic != (byte) 0xdf){
            throw new RuntimeException("invalid magic number: " + magic);
        }

        byte v = byteBuf.readByte();
        this.ver = ProtocolVersion.find(v);
        if(ver == null){
            throw new RuntimeException("Unknown protocol version: " + v);
        }

        this.sender = byteBuf.readLong();
        this.receiver = byteBuf.readLong();

        this.reserved = byteBuf.readByte();

        byte c = byteBuf.readByte();
        this.cmd = Command.find(c);
        if(cmd == null){
            throw new UnsupportedOperationException("Unsupported operation command" + c);
        }

        this.length = byteBuf.readInt();

        //从右往左获取第0位的比特位 该位为群聊标识位
        int bitValue = bitPosition(this.reserved, 0);
        this.groupFlag = bitValue == 1;
    }

    //获取指定比特位的值
    private int bitPosition(int num,int n){
        int mask  = 1 << n;
        return (num & mask) >> n;
    }

    @JsonIgnore
    private byte setBitValue(byte b,int n,byte v){
        byte mask = (byte) (1 << n);
        if(v == 1){
            b = (byte) (b | mask);
        }else {
            b = (byte) (b & ~mask);
        }
        return b;
    }

    public void encodeAsByteBuf(ByteBuf byteBuf) {
        if(this.magic != (byte) 0xdf){
            throw new RuntimeException("magic error");
        }
        byteBuf.writeByte(this.magic);
        byteBuf.writeByte(this.ver.getVer());
        byteBuf.writeLong(this.sender);
        byteBuf.writeLong(this.receiver);
        byteBuf.writeByte(this.reserved);
        byteBuf.writeByte(this.cmd.getCmdCode());
        byteBuf.writeInt(this.length);
    }

    public static int headLength(){

        return 24;
    }
}
