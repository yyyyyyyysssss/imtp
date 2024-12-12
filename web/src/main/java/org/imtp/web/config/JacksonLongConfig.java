package org.imtp.web.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * @Description 返回值 Long转为String
 * @Author ys
 * @Date 2024/12/8 14:06
 */
@Configuration
public class JacksonLongConfig implements Jackson2ObjectMapperBuilderCustomizer {

    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
        builder.serializerByType(Long.class, ToStringSerializer.instance);
        builder.serializerByType(long.class, ToStringSerializer.instance);
    }

}
