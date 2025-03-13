package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.enums.DeliveryMethod;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/22 21:06
 */
public class WebSocketAdapterSystemMessage extends AbstractSystemMessage{

    public WebSocketAdapterSystemMessage(WebSocketMessage webSocketMessage){
        super(webSocketMessage.getSender(),
                webSocketMessage.getReceiver(),
                Command.find(webSocketMessage.getType().getValue().byteValue()));
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

    @Override
    public int getBodyLength0() {
        return 0;
    }
}
