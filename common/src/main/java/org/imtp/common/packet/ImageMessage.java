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

    public ImageMessage(String url,MessageMetadata messageMetadata, long sender, long receiver, Long ackId, boolean groupFlag) {
        super(url,messageMetadata,0, sender, receiver, Command.IMAGE_MESSAGE, ackId, groupFlag);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

    public String getUrl(){

        return this.text;
    }

}
