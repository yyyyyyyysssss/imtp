package org.imtp.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.imtp.common.packet.AbstractMessage;
import org.imtp.common.packet.TextMessageV2;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum MessageTypeV2 {

    TEXT(1, TextMessageV2::new),

    ;
    private final Integer value;
    private final Supplier<? extends AbstractMessage> factory;

    MessageTypeV2(int value, Supplier<? extends AbstractMessage> factory) {
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

    public AbstractMessage create() {
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
