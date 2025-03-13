package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.enums.DeliveryMethod;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/22 21:06
 */
public class WebSocketAdapterSignalingMessage extends AbstractSignalingMessage{

    public WebSocketAdapterSignalingMessage(WebSocketMessage webSocketMessage){
        super(webSocketMessage.getContent(),
                webSocketMessage.getSender(),
                webSocketMessage.getReceiver(),
                Command.find(webSocketMessage.getType().getValue().byteValue()),
                webSocketMessage.getDeliveryMethod().equals(DeliveryMethod.GROUP));
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }
}
