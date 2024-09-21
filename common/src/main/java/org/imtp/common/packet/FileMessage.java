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
public class FileMessage extends AbstractTextMessage{

    public FileMessage(){}

    public FileMessage(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    public FileMessage(String path, long sender, long receiver, Long ackId, boolean groupFlag) {
        super(path, sender, receiver, Command.FILE_MESSAGE, ackId, groupFlag);
    }

    public FileMessage(WebSocketMessage webSocketMessage){
        super(webSocketMessage.getContent(), webSocketMessage.getSender(), webSocketMessage.getReceiver(), Command.FILE_MESSAGE, webSocketMessage.getAckId(), webSocketMessage.getDeliveryMethod().equals(DeliveryMethod.GROUP));
    }

    public FileMessage(String path, long sender, long receiver, Long ackId) {
        super(path, sender, receiver, Command.FILE_MESSAGE, ackId, false);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

    public String getPath(){

        return this.text;
    }

}
