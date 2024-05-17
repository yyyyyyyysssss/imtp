package org.imtp.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum MessageType {

    TEXT_MESSAGE((int) Command.TEXT_MESSAGE.getCmdCode()),
    VOICE_MESSAGE((int) Command.VOICE_MESSAGE.getCmdCode()),
    MEME_MESSAGE((int) Command.MEME_MESSAGE.getCmdCode()),
    IMAGE_MESSAGE((int) Command.IMAGE_MESSAGE.getCmdCode()),
    VIDEO_MESSAGE((int) Command.VIDEO_MESSAGE.getCmdCode()),
    FILE_MESSAGE((int) Command.FILE_MESSAGE.getCmdCode());

    private final Integer value;

    MessageType(Integer value) {
        this.value = value;
    }

    @JsonValue
    public Integer getValue() {
        return value;
    }

    public static MessageType findMessageTypeByValue(Integer value){
        MessageType[] values = MessageType.values();
        for (MessageType messageType : values){
            if(messageType.value.equals(value)){
                return messageType;
            }
        }
        return null;
    }
}
