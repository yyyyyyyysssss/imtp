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
public class SelfUserInfoVO {


    private String sub;

    private String name;

    private String gender;

    @JsonProperty("preferred_username")
    private String preferredUsername;

    private String picture;

    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @Override
    public String toString() {
        return "SelfUserInfoVO{" +
                "sub='" + sub + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", preferredUsername='" + preferredUsername + '\'' +
                ", picture='" + picture + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
