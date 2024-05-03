package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/29 15:58
 */
public class FriendshipRequest extends SystemTextMessage{

    public FriendshipRequest(Long sender) {
        super(sender, 0, Command.FRIENDSHIP_REQ);
    }

    public FriendshipRequest(ByteBuf byteBuf, Header header) {
        super(byteBuf,header);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

    @Override
    public int getBodyLength0() {
        return 0;
    }
}
