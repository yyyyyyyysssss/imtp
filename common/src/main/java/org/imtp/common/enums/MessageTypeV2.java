package org.imtp.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.imtp.common.packet.*;
import org.imtp.common.packet.base.AbstractMessagePacket;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum MessageTypeV2 {

    TEXT(1, TextMessageV2::new),
    VOICE((byte) 2, VoiceMessageV2::new),
    MEME((byte) 3, MemeMessageV2::new),
    IMAGE(4, ImageMessageV2::new),
    VIDEO((byte) 5, VideoMessageV2::new),
    FILE((byte) 6,FileMessageV2::new),


    ;
    private final Integer value;
    private final Supplier<? extends AbstractMessagePacket> factory;

    MessageTypeV2(int value, Supplier<? extends AbstractMessagePacket> factory) {
        this.value = value;
        this.factory = factory;
    }

    private static final Map<Integer, MessageTypeV2> VALUE_MAP = Stream.of(values())
            .collect(Collectors.toUnmodifiableMap(
                    MessageTypeV2::getValue,
                    v -> v
            ));

    @JsonValue
    public Integer getValue() {
        return value;
    }

    public AbstractMessagePacket create() {
        return factory.get();
    }

    public static MessageTypeV2 findByValue(int value) {
        MessageTypeV2 type = VALUE_MAP.get(value);
        if (type == null) {
            throw new IllegalArgumentException("Unknown MessageTypeV2 value: " + value);
        }
        return type;
    }
}
