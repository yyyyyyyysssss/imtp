package org.imtp.web.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @Description 基于请求路径权限认证
 * @Author ys
 * @Date 2023/10/10 16:51
 */
public class RequestUrlAuthority implements GrantedAuthority {

    public RequestUrlAuthority() {
    }

    public RequestUrlAuthority(String code) {
        this(code,null);
    }

    public RequestUrlAuthority(String code, String urls) {
        this.code = code;
        this.urls = urls;
    }

    //权限编码
    private String code;

    //该权限可访问的urls 多个以,号隔开
    private String urls;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    @Override
    public String getAuthority() {
        return this.code;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    @JsonDeserialize(using = RequestUrlAuthorityDeserializer.class)
    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public abstract static class RequestUrlAuthorityMixin {
    }

    static class RequestUrlAuthorityDeserializer extends JsonDeserializer<RequestUrlAuthority> {

        @Override
        public RequestUrlAuthority deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
            JsonNode root = mapper.readTree(jsonParser);
            RequestUrlAuthority requestAuthority = new RequestUrlAuthority();
            String code = JsonNodeUtils.findStringValue(root, "code");
            String urls = JsonNodeUtils.findStringValue(root, "urls");

            requestAuthority.setCode(code);
            requestAuthority.setUrls(urls);
            return requestAuthority;
        }
    }

    abstract static class JsonNodeUtils {

        static final TypeReference<Set<String>> STRING_SET = new TypeReference<Set<String>>() {
        };

        static final TypeReference<Map<String, Object>> STRING_OBJECT_MAP = new TypeReference<Map<String, Object>>() {
        };

        static String findStringValue(JsonNode jsonNode, String fieldName) {
            if (jsonNode == null) {
                return null;
            }
            JsonNode value = jsonNode.findValue(fieldName);
            return (value != null && value.isTextual()) ? value.asText() : null;
        }

        static <T> T findValue(JsonNode jsonNode, String fieldName, TypeReference<T> valueTypeReference,
                               ObjectMapper mapper) {
            if (jsonNode == null) {
                return null;
            }
            JsonNode value = jsonNode.findValue(fieldName);
            return (value != null && value.isContainerNode()) ? mapper.convertValue(value, valueTypeReference) : null;
        }

        static JsonNode findObjectNode(JsonNode jsonNode, String fieldName) {
            if (jsonNode == null) {
                return null;
            }
            JsonNode value = jsonNode.findValue(fieldName);
            return (value != null && value.isObject()) ? value : null;
        }

    }

}
