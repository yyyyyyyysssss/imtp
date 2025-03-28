package org.imtp.common.packet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/21 12:50
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoiceMessage extends AbstractTextMessage{

    public VoiceMessage(){}

    public VoiceMessage(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    public VoiceMessage(String url, MessageMetadata messageMetadata, long sender, long receiver, Long ackId, boolean groupFlag) {
        super(url,messageMetadata, sender, receiver, Command.VOICE_MESSAGE, ackId, groupFlag);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

    public String getUrl(){

        return this.text;
    }

}
