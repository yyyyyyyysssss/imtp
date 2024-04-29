package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/29 15:58
 */
public class PullFriendRequest extends SystemTextMessage{

    public PullFriendRequest(Long sender) {
        super(sender, 0, Command.PULL_FRIEND_REQ);
    }

    public PullFriendRequest(ByteBuf byteBuf,Header header) {
        super(byteBuf,header);
    }

    @Override
    void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }
}
