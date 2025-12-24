package org.imtp.common.packet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.enums.MessageTypeV2;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.MessagePacket;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/21 12:50
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoiceMessageV2 extends MessagePacket {

    public VoiceMessageV2(){
        super();
    }

    public VoiceMessageV2(Header header) {
        super(header);
    }

    public VoiceMessageV2(String text, long sender, long receiver, Long ackId, boolean groupFlag) {
        super(text, MessageTypeV2.VOICE, null, sender, receiver, ackId, groupFlag);
    }

    public String getUrl() {
        return this.content;
    }

}
