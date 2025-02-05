package org.imtp.web.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/3 23:44
 */
@Getter
@Setter
public class MicrosoftUserInfoVO implements OAuthVO{


    private String sub;

    private String name;

    @JsonProperty("family_name")
    private String family_name;

    @JsonProperty("given_name")
    private String given_name;

    private String picture;

    private String email;

    @Override
    public String toString() {
        return "MicrosoftUserInfoVO{" +
                "sub='" + sub + '\'' +
                ", name='" + name + '\'' +
                ", family_name='" + family_name + '\'' +
                ", given_name='" + given_name + '\'' +
                ", picture='" + picture + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public String getUsername() {
        return this.sub;
    }

    @Override
    public String getNickname() {
        return this.name;
    }

    @Override
    public String getAvatar() {
        return this.picture;
    }

    @Override
    public String getEmail() {
        return this.email;
    }
}
