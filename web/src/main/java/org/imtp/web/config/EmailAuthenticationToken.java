package org.imtp.web.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.imtp.web.config.oauth2.OAuthClientAuthenticationToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/13 11:36
 */
public class EmailAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private Object credentials;

    public EmailAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.setAuthenticated(false);
    }

    public EmailAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }


    public static EmailAuthenticationToken unauthenticated(Object principal, Object credentials) {
        return new EmailAuthenticationToken(principal, credentials);
    }

    public static EmailAuthenticationToken authenticated(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        return new EmailAuthenticationToken(principal, credentials, authorities);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    @JsonDeserialize(using = EmailAuthenticationToken.EmailAuthenticationTokenDeserializer.class)
    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public abstract static class EmailAuthenticationTokenMixin {
    }

    static class EmailAuthenticationTokenDeserializer extends JsonDeserializer<EmailAuthenticationToken> {

        private static final TypeReference<List<GrantedAuthority>> GRANTED_AUTHORITY_LIST = new TypeReference<>() {
        };
        private static final TypeReference<Object> OBJECT = new TypeReference<>() {
        };

        @Override
        public EmailAuthenticationToken deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            ObjectMapper mapper = (ObjectMapper)jsonParser.getCodec();
            JsonNode jsonNode = (JsonNode)mapper.readTree(jsonParser);
            boolean authenticated = this.readJsonNode(jsonNode, "authenticated").asBoolean();
            JsonNode principalNode = this.readJsonNode(jsonNode, "principal");
            Object principal = this.getPrincipal(mapper, principalNode);
            JsonNode credentialsNode = this.readJsonNode(jsonNode, "credentials");
            Object credentials = this.getCredentials(credentialsNode);
            List<GrantedAuthority> authorities = (List)mapper.readValue(this.readJsonNode(jsonNode, "authorities").traverse(mapper), GRANTED_AUTHORITY_LIST);
            EmailAuthenticationToken token = !authenticated ? EmailAuthenticationToken.unauthenticated(principal, credentials) : EmailAuthenticationToken.authenticated(principal, credentials, authorities);
            JsonNode detailsNode = this.readJsonNode(jsonNode, "details");
            if (!detailsNode.isNull() && !detailsNode.isMissingNode()) {
                Object details = mapper.readValue(detailsNode.toString(), OBJECT);
                token.setDetails(details);
            } else {
                token.setDetails((Object)null);
            }
            return token;
        }

        private Object getCredentials(JsonNode credentialsNode) {
            return !credentialsNode.isNull() && !credentialsNode.isMissingNode() ? credentialsNode.asText() : null;
        }

        private Object getPrincipal(ObjectMapper mapper, JsonNode principalNode) throws IOException {
            return principalNode.isObject() ? mapper.readValue(principalNode.traverse(mapper), Object.class) : principalNode.asText();
        }

        private JsonNode readJsonNode(JsonNode jsonNode, String field) {
            return (JsonNode)(jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance());
        }

    }
}
