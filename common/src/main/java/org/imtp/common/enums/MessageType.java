package org.imtp.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static org.imtp.common.enums.Command.*;

@Getter
public enum MessageType {

    TEXT_MESSAGE((int) Command.TEXT_MESSAGE.getCmdCode()),
    VOICE_MESSAGE((int) Command.VOICE_MESSAGE.getCmdCode()),
    MEME_MESSAGE((int) Command.MEME_MESSAGE.getCmdCode()),
    IMAGE_MESSAGE((int) Command.IMAGE_MESSAGE.getCmdCode()),
    VIDEO_MESSAGE((int) Command.VIDEO_MESSAGE.getCmdCode()),
    FILE_MESSAGE((int) Command.FILE_MESSAGE.getCmdCode()),
    SIGNALING_OFFER((int) Command.SIGNALING_OFFER.getCmdCode()),
    SIGNALING_ANSWER((int) Command.SIGNALING_ANSWER.getCmdCode()),
    SIGNALING_CANDIDATE((int) Command.SIGNALING_CANDIDATE.getCmdCode()),
    SIGNALING_BUSY((int) Command.SIGNALING_BUSY.getCmdCode()),
    SIGNALING_CLOSE((int) Command.SIGNALING_CLOSE.getCmdCode()),

    ;
    private final Integer value;

    private static final Map<Integer, MessageType> cacheMap = new HashMap<Integer, MessageType>();

    MessageType(Integer value) {
        this.value = value;
    }

    @JsonValue
    public Integer getValue() {
        return value;
    }

    public static MessageType findMessageTypeByValue(Integer value){
        MessageType type;
        if ((type = cacheMap.get(value)) != null){
            return type;
        }
        MessageType[] values = MessageType.values();
        for (MessageType messageType : values){
            if(messageType.value.equals(value)){
                cacheMap.put(value, messageType);
                return messageType;
            }
        }
        throw new IllegalArgumentException("No such messageType: " + value);
    }
}
