package org.imtp.packet;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.imtp.enums.Command;

/**
 * @Description
 * @Author ys
 * @Date 2023/4/2 13:49
 */
@Getter
@Setter
public abstract class Packet{

    protected Header header;

    public Packet(long sender, long receiver, Command command,int bodyLength){
        this(new Header(sender,receiver,command,bodyLength));
    }

    public Packet(Header header){
        this.header = header;
    }

    public void encodeAsByteBuf(ByteBuf byteBuf) {
        this.header.encodeAsByteBuf(byteBuf);
        encodeBodyAsByteBuf(byteBuf);
    }

    public abstract void encodeBodyAsByteBuf(ByteBuf byteBuf);

    public Long getSender(){

        return this.header.getSender();
    }

    public Long getReceiver(){

        return this.header.getReceiver();
    }

}
