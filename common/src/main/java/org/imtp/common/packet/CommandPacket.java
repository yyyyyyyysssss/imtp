package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/21 12:50
 */
@Getter
@Setter
public class CommandPacket extends Packet{

    private byte[] bytes;


    public CommandPacket(Header header,ByteBuf byteBuf) {
        super(header);
        this.bytes = new byte[header.getLength()];
        byteBuf.readBytes(bytes);
    }

    @Override
    public void encodeBodyAsByteBuf(ByteBuf byteBuf) {
        byteBuf.writeBytes(bytes);
    }
}
