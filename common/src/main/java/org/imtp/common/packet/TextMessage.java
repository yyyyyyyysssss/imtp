package org.imtp.common.packet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TextMessage extends AbstractTextMessage {

    public TextMessage() {
    }

    public TextMessage(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    public TextMessage(String message, long sender, long receiver, Long ackId, boolean groupFlag) {
        super(message,null,0, sender, receiver, Command.TEXT_MESSAGE, ackId, groupFlag);
    }

    public TextMessage(String message, long sender, long receiver, Long ackId) {
        super(message,null,0, sender, receiver, Command.TEXT_MESSAGE, ackId, false);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }
}
