package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.Packet;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/21 12:50
 */
@Getter
@Setter
public class CommandPacket extends Packet {

    private byte[] bytes;


    public CommandPacket(Header header, byte[] bytes, short verify) {
        super(header);
        this.bytes = bytes;
        this.verify = verify;
    }

    @Override
    public void encodeBodyAsByteBuf(ByteBuf byteBuf) {
        byteBuf.writeBytes(bytes);
    }

    @Override
    public int getBodyLength() {
        return bytes.length;
    }
}
