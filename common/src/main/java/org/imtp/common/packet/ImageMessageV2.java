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
 * @Date 2024/6/25 11:40
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageMessageV2 extends MessagePacket {

    public ImageMessageV2(){
        super();
    }

    public ImageMessageV2(Header header) {
        super(header);
    }

    public ImageMessageV2(String text,MessageMetadata contentMetadata, long sender, long receiver, Long ackId, boolean groupFlag) {
        super(text, MessageTypeV2.IMAGE, contentMetadata, sender, receiver, ackId, groupFlag);
    }

    public String getUrl() {
        return this.content;
    }

}
