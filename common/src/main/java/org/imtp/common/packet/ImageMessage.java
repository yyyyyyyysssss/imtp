package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.packet.base.Header;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/25 11:40
 */
public class ImageMessage extends AbstractTextMessage{

    public ImageMessage(){}

    public ImageMessage(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    public ImageMessage(String url, long sender, long receiver, Long ackId, boolean groupFlag) {
        super(url, sender, receiver, Command.IMAGE_MESSAGE, ackId, groupFlag);
    }

    public ImageMessage(WebSocketMessage webSocketMessage){
        super(webSocketMessage.getContent(), webSocketMessage.getSender(), webSocketMessage.getReceiver(), Command.IMAGE_MESSAGE, webSocketMessage.getAckId(), webSocketMessage.getDeliveryMethod().equals(DeliveryMethod.GROUP));
    }

    public ImageMessage(String url, long sender, long receiver, Long ackId) {
        super(url, sender, receiver, Command.IMAGE_MESSAGE, ackId, false);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

    public String getUrl(){

        return this.text;
    }

}
