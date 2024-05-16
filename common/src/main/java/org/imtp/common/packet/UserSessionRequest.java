package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/16 17:10
 */
public class UserSessionRequest extends SystemTextMessage{

    public UserSessionRequest(Long sender) {
        super(sender, 0, Command.USER_SESSION_REQ);
    }

    public UserSessionRequest(ByteBuf byteBuf, Header header) {
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
