package org.imtp.common.packet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.imtp.common.enums.MessageTypeV2;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.MessagePacket;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/21 12:50
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemeMessageV2 extends MessagePacket {

    public MemeMessageV2(){
        super();
    }

    public MemeMessageV2(Header header) {
        super(header);
    }

    public MemeMessageV2(String text, long sender, long receiver, Long ackId, boolean groupFlag) {
        super(text, MessageTypeV2.MEME, null, sender, receiver, ackId, groupFlag);
    }

    public String getUrl() {
        return this.content;
    }

}
