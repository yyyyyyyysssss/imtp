package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/29 15:58
 */
public class GroupRelationshipRequest extends SystemTextMessage{

    public GroupRelationshipRequest(Long sender) {
        super(sender, 0, Command.GROUP_RELATIONSHIP_REQ);
    }

    public GroupRelationshipRequest(ByteBuf byteBuf, Header header) {
        super(byteBuf,header);
    }

    @Override
    void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }
}
