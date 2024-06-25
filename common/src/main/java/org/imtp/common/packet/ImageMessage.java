package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/25 11:40
 */
public class ImageMessage extends AbstractTextMessage{

    public ImageMessage(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    public ImageMessage(String path, long sender, long receiver, Long ackId, boolean groupFlag) {
        super(path, sender, receiver, Command.IMAGE_MESSAGE, ackId, groupFlag);
    }

    public ImageMessage(String path, long sender, long receiver, Long ackId) {
        super(path, sender, receiver, Command.IMAGE_MESSAGE, ackId, false);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

    public String getPath(){

        return this.text;
    }

}
