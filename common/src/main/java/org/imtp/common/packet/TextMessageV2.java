package org.imtp.common.packet;

import org.imtp.common.enums.MessageTypeV2;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.MessagePacket;


public class TextMessageV2 extends MessagePacket {

    public TextMessageV2(){
        super();
    }

    public TextMessageV2(Header header) {
        super(header);
    }

    public TextMessageV2(String text, long sender, long receiver, Long ackId, boolean groupFlag) {
        super(text,MessageTypeV2.TEXT, null, sender, receiver, ackId, groupFlag);
    }

    public String getText() {
        return this.content;
    }
}
