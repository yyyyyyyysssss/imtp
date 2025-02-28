package org.imtp.common.packet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;

/**
 * @Description
 * @Author ys
 * @Date 2025/2/24 16:39
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignalingCloseMessage extends AbstractSignalingMessage{

    public SignalingCloseMessage() {
    }

    public SignalingCloseMessage(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    public SignalingCloseMessage(long sender, long receiver, boolean groupFlag) {
        super(null, sender, receiver, Command.SIGNALING_CLOSE, groupFlag);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }
}
