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
public class VideoCallMessage extends AbstractTextMessage{

    public VideoCallMessage(){}

    public VideoCallMessage(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    public VideoCallMessage(String path, MessageMetadata messageMetadata, long sender, long receiver, Long ackId, boolean groupFlag) {
        super(path,messageMetadata,0, sender, receiver, Command.VIDEO_CALL_MESSAGE, ackId, groupFlag);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

    public String getUrl(){

        return this.text;
    }

}
