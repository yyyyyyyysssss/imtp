package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;

public class TextMessage extends AbstractTextMessage{
    public TextMessage(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    public TextMessage(String message, long sender, long receiver, boolean groupFlag) {
        super(message, sender, receiver, Command.TEXT_MESSAGE, groupFlag);
    }

    public TextMessage(String message, long sender, long receiver) {
        super(message, sender, receiver, Command.TEXT_MESSAGE,false);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }
}
