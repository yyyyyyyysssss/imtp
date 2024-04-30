package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/30 14:02
 */
public class OfflineMessageRequest extends SystemTextMessage{


    public OfflineMessageRequest(long sender) {
        super(sender, 0, Command.OFFLINE_MSG_REQ);
    }

    public OfflineMessageRequest(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    @Override
    void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }
}
