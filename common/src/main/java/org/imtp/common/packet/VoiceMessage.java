package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/21 12:50
 */
public class VoiceMessage extends AbstractTextMessage{

    public VoiceMessage(){}

    public VoiceMessage(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    public VoiceMessage(String url, MessageMetadata messageMetadata, long sender, long receiver, Long ackId, boolean groupFlag) {
        super(url,messageMetadata,0, sender, receiver, Command.VOICE_MESSAGE, ackId, groupFlag);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

    public String getUrl(){

        return this.text;
    }

}
