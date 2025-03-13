package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;

/**
 * @Description
 * @Author ys
 * @Date 2025/3/12 16:34
 */
public class HeartbeatPongMessage extends AbstractSystemMessage {

    public HeartbeatPongMessage() {
        super(0, 0, Command.HEARTBEAT_PONG);
    }

    public HeartbeatPongMessage(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

    @Override
    public int getBodyLength0() {
        return 0;
    }
}
