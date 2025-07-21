package org.imtp.api.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class MobileSensitiveSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (value != null && !value.isEmpty()) {
            // 脱敏逻辑：手机号脱敏，保留前三位和后四位，中间用****替换
            String maskedValue = value.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            gen.writeString(maskedValue);
        } else {
            gen.writeString(value);
        }
    }
}
