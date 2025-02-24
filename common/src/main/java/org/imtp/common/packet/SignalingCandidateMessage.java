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
public class SignalingCandidateMessage extends AbstractTextMessage{

    public SignalingCandidateMessage() {
    }

    public SignalingCandidateMessage(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    public SignalingCandidateMessage(String message, long sender, long receiver, Long ackId, boolean groupFlag) {
        super(message,null,0, sender, receiver, Command.SIGNALING_CANDIDATE, ackId, groupFlag);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

}
