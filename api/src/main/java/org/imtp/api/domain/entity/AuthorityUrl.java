package org.imtp.api.domain.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.imtp.api.domain.validation.ValidApiUrls;
import org.imtp.api.utils.JsonNodeUtils;

import java.io.IOException;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/30 9:05
 */
@Getter
@Setter
public class AuthorityUrl {

    @NotBlank(message = "请求方法不能为空")
    @Pattern(regexp = "^(?i)(GET|POST|PUT|PATCH|DELETE|\\*)$", message = "请求方法仅支持 GET、POST、PUT、PATCH、DELETE 或 *")
    private String method;

    @NotBlank(message = "路径不能为空")
    @ValidApiUrls(message = "路径不合法")
    private String url;


    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    @JsonDeserialize(using = AuthorityUrl.AuthorityUrlDeserializer.class)
    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public abstract static class AuthorityUrlMixin {
    }

    static class AuthorityUrlDeserializer extends JsonDeserializer<AuthorityUrl> {

        @Override
        public AuthorityUrl deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
            JsonNode root = mapper.readTree(jsonParser);
            AuthorityUrl authorityUrl = new AuthorityUrl();
            String method = JsonNodeUtils.findStringValue(root, "method");
            String url = JsonNodeUtils.findStringValue(root, "url");
            authorityUrl.setMethod(method);
            authorityUrl.setUrl(url);
            return authorityUrl;
        }
    }

}
