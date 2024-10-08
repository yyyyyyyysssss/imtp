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


    public FileMessage(String url, long sender, long receiver, Long ackId, boolean groupFlag) {
        this(url,null, sender, receiver, ackId, groupFlag);
    }

    public FileMessage(String url,MessageMetadata messageMetadata, long sender, long receiver, Long ackId, boolean groupFlag) {
        super(url,messageMetadata, sender, receiver, Command.FILE_MESSAGE, ackId, groupFlag);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

    public String getUrl(){

        return this.text;
    }

}
