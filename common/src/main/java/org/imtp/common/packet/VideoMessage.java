package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.packet.base.Header;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/21 12:50
 */
public class VideoMessage extends AbstractTextMessage{

    public VideoMessage(){}

    public VideoMessage(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    public VideoMessage(String path, long sender, long receiver, Long ackId, boolean groupFlag) {
        super(path, sender, receiver, Command.VIDEO_MESSAGE, ackId, groupFlag);
    }

    public VideoMessage(WebSocketMessage webSocketMessage){
        super(webSocketMessage.getContent(), webSocketMessage.getSender(), webSocketMessage.getReceiver(), Command.VIDEO_MESSAGE, webSocketMessage.getAckId(), webSocketMessage.getDeliveryMethod().equals(DeliveryMethod.GROUP));
    }

    public VideoMessage(String path, long sender, long receiver, Long ackId) {
        super(path, sender, receiver, Command.VIDEO_MESSAGE, ackId, false);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

    public String getUrl(){

        return this.text;
    }

}
