package org.imtp.common.packet;

import org.imtp.common.enums.MessageTypeV2;


public class TextMessageV2 extends AbstractStringMessage {


    public TextMessageV2() {

    }

    public TextMessageV2(String text) {
        super(text);
    }

    @Override
    public MessageTypeV2 getMessageType() {
        return MessageTypeV2.TEXT;
    }

    public String getText() {
        return this.content;
    }
}
