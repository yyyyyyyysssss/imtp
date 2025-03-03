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
public class SignalingBusyMessage extends AbstractSignalingMessage{

    public SignalingBusyMessage() {
    }

    public SignalingBusyMessage(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    public SignalingBusyMessage(long sender, long receiver, boolean groupFlag) {
        super(null, sender, receiver, Command.SIGNALING_BUSY, groupFlag);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }
}
