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
public class GoogleUserInfoVO {


    private String sub;

    private String name;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    private String picture;

    private String email;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    @Override
    public String toString() {
        return "GoogleUserInfoVO{" +
                "sub='" + sub + '\'' +
                ", name='" + name + '\'' +
                ", givenName='" + givenName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", picture='" + picture + '\'' +
                ", email='" + email + '\'' +
                ", emailVerified=" + emailVerified +
                '}';
    }
}
