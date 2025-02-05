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
import org.imtp.common.enums.ClientType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/11/9 22:37
 */
public class RefreshAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private Object credentials;
    private ClientType clientType;

    public RefreshAuthenticationToken(Object principal, Object credentials,ClientType clientType) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.clientType = clientType;
        this.setAuthenticated(false);
    }

    public RefreshAuthenticationToken(Object principal, Object credentials,ClientType clientType, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.clientType = clientType;
        super.setAuthenticated(true);
    }

    public static RefreshAuthenticationToken unauthenticated(Object principal, Object credentials,ClientType clientType) {
        return new RefreshAuthenticationToken(principal, credentials,clientType);
    }

    public static RefreshAuthenticationToken authenticated(Object principal, Object credentials,ClientType clientType, Collection<? extends GrantedAuthority> authorities) {
        return new RefreshAuthenticationToken(principal, credentials, clientType, authorities);
    }

    public ClientType getClientType() {
        return clientType;
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
    @JsonDeserialize(using = RefreshAuthenticationToken.RefreshAuthenticationTokenDeserializer.class)
    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public abstract static class RefreshAuthenticationTokenMixin {
    }

    static class RefreshAuthenticationTokenDeserializer extends JsonDeserializer<RefreshAuthenticationToken> {

        private static final TypeReference<List<GrantedAuthority>> GRANTED_AUTHORITY_LIST = new TypeReference<>() {
        };
        private static final TypeReference<Object> OBJECT = new TypeReference<>() {
        };

        @Override
        public RefreshAuthenticationToken deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            ObjectMapper mapper = (ObjectMapper)jsonParser.getCodec();
            JsonNode jsonNode = (JsonNode)mapper.readTree(jsonParser);
            boolean authenticated = this.readJsonNode(jsonNode, "authenticated").asBoolean();
            JsonNode principalNode = this.readJsonNode(jsonNode, "principal");
            Object principal = this.getPrincipal(mapper, principalNode);
            JsonNode credentialsNode = this.readJsonNode(jsonNode, "credentials");
            Object credentials = this.getCredentials(credentialsNode);
            JsonNode clientTypeNode = this.readJsonNode(jsonNode, "clientType");
            ClientType clientType = this.getClientType(clientTypeNode);
            List<GrantedAuthority> authorities = (List)mapper.readValue(this.readJsonNode(jsonNode, "authorities").traverse(mapper), GRANTED_AUTHORITY_LIST);
            RefreshAuthenticationToken token = !authenticated ? RefreshAuthenticationToken.unauthenticated(principal, credentials,clientType) : RefreshAuthenticationToken.authenticated(principal, credentials,clientType, authorities);
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

        private ClientType getClientType(JsonNode clientTypeNode) {
            return !clientTypeNode.isNull() && !clientTypeNode.isMissingNode() ? ClientType.valueOf(clientTypeNode.asText()) : null;
        }

        private JsonNode readJsonNode(JsonNode jsonNode, String field) {
            return (JsonNode)(jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance());
        }

    }

}
