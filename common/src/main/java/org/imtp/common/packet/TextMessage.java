package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.packet.base.Header;

public class TextMessage extends AbstractTextMessage {

    public TextMessage() {
    }

    public TextMessage(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    public TextMessage(String message, long sender, long receiver, Long ackId, boolean groupFlag) {
        super(message, sender, receiver, Command.TEXT_MESSAGE, ackId, groupFlag);
    }

    public TextMessage(WebSocketMessage webSocketMessage) {
        super(webSocketMessage.getContent(), webSocketMessage.getSender(), webSocketMessage.getReceiver(), Command.TEXT_MESSAGE, webSocketMessage.getAckId(), webSocketMessage.getDeliveryMethod().equals(DeliveryMethod.GROUP));
    }

    public TextMessage(String message, long sender, long receiver, Long ackId) {
        super(message, sender, receiver, Command.TEXT_MESSAGE, ackId, false);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }
}
