package org.imtp.web.config;

import com.fasterxml.jackson.annotation.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @Description
 * @Author ys
 * @Date 2025/2/21 16:12
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY
)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.ANY
)
@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class OneTimeTokenAuthenticationTokenMixin {

    @JsonCreator
    OneTimeTokenAuthenticationTokenMixin(@JsonProperty("principal") Object principal, @JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities) {
    }

}
