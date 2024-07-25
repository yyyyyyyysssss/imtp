package org.imtp.web.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;
import org.imtp.web.enums.ClientType;

import java.util.Objects;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/16 13:48
 */
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenInfo {

    @Tolerate
    public TokenInfo(){}

    private Long id;

    private Long userId;

    private String accessToken;

    private String refreshToken;

    private ClientType clientType;

    private Long expiration;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        TokenInfo token = (TokenInfo) object;
        return Objects.equals(id, token.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TokenInfo{" +
                "id=" + id +
                ", userId=" + userId +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", clientType=" + clientType +
                ", expiration=" + expiration +
                '}';
    }
}
