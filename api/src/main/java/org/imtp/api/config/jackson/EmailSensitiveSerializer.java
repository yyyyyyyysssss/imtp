package org.imtp.api.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class EmailSensitiveSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (value != null && !value.isEmpty()) {
            int index = value.indexOf("@");
            if (index > 2) {
                // 获取前两个字符
                String prefix = value.substring(0, 2);
                // 获取"@"后面的域名
                String domain = value.substring(index);
                // 中间部分替换为*
                String maskedEmail = prefix + "*".repeat(index - 2) + domain;
                gen.writeString(maskedEmail);
            } else {
                // 如果邮箱长度小于等于2字符，直接返回原值
                gen.writeString(value);
            }
        } else {
            gen.writeString(value);
        }
    }

}
