package org.imtp.api.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class IdCardSensitiveSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (value != null && !value.isEmpty()) {
            // 脱敏逻辑：身份证号脱敏，保留前四位和后四位，中间用*替换
            String maskedValue = value.replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1**********$2");
            gen.writeString(maskedValue);
        } else {
            gen.writeString(value);
        }
    }

}
