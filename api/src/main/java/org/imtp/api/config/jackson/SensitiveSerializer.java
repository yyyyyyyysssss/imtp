package org.imtp.api.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;

public class SensitiveSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private JsonSerializer<String> serializer;

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if(SensitiveContextHolder.isSensitive()){
            if (serializer != null) {
                serializer.serialize(s, jsonGenerator, serializerProvider);
            } else {
                jsonGenerator.writeString(s);  // 默认不脱敏
            }
        }else {
            jsonGenerator.writeString(s);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            Sensitive sensitive = beanProperty.getAnnotation(Sensitive.class);
            if (sensitive != null) {
                // 根据 SensitiveType 创建对应的序列化器
                this.serializer = getSerializerByType(sensitive.value());
            }
        }
        return this;
    }

    private JsonSerializer<String> getSerializerByType(SensitiveType type) {
        switch (type) {
            case MOBILE:
                return new MobileSensitiveSerializer();
            case EMAIL:
                return new EmailSensitiveSerializer();
            case ID_CARD:
                return new IdCardSensitiveSerializer();
            default:
                return null;
        }
    }

}
