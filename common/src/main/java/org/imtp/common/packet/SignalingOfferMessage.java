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
public class SignalingOfferMessage extends AbstractSignalingMessage{

    public SignalingOfferMessage() {
    }

    public SignalingOfferMessage(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    public SignalingOfferMessage(String message, long sender, long receiver, boolean groupFlag) {
        super(message,sender, receiver, Command.SIGNALING_OFFER, groupFlag);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

}
