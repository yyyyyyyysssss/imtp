package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.enums.DeliveryMethod;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/22 21:06
 */
public class WebSocketAdapterMessage extends AbstractTextMessage{

    public WebSocketAdapterMessage(WebSocketMessage webSocketMessage){
        super(webSocketMessage.getContent(),
                webSocketMessage.getContentMetadata(),
                webSocketMessage.getSessionId(),
                webSocketMessage.getSender(),
                webSocketMessage.getReceiver(),
                Command.find(webSocketMessage.getType().getValue().byteValue()),
                webSocketMessage.getAckId(),
                webSocketMessage.getDeliveryMethod().equals(DeliveryMethod.GROUP));
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }
}
