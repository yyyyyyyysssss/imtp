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
public class SignalingAnswerMessage extends AbstractSignalingMessage{

    public SignalingAnswerMessage() {
    }

    public SignalingAnswerMessage(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    public SignalingAnswerMessage(String message, long sender, long receiver, boolean groupFlag) {
        super(message, sender, receiver, Command.SIGNALING_ANSWER, groupFlag);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }
}
