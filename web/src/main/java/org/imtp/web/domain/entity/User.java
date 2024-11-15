package org.imtp.web.domain.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
import lombok.*;
import org.imtp.common.enums.Gender;
import org.imtp.web.config.RequestUrlAuthority;
import org.imtp.web.utils.JsonNodeUtil;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/25 12:33
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_user")
public class User implements UserDetails, CredentialsContainer {

    @TableField(exist = false)
    private List<? extends GrantedAuthority> authorities;

    @TableId
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("nickname")
    private String nickname;

    @TableField("note")
    private String note;

    @TableField("tagline")
    private String tagline;

    @TableField("gender")
    @EnumValue
    private Gender gender;

    @TableField("avatar")
    private String avatar;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;

    @TableField("region")
    private String region;

    @TableField("create_time")
    private Date createTime;

    @Override
    public String toString() {
        return "User{" +
                "authorities=" + authorities +
                ", id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", note='" + note + '\'' +
                ", tagline='" + tagline + '\'' +
                ", gender=" + gender +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", region='" + region + '\'' +
                ", createTime=" + createTime +
                '}';
    }

    @Override
    public void eraseCredentials() {

    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = new ArrayList<>(authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    @JsonDeserialize(using = User.UserDeserializer.class)
    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public abstract static class RequestUrlAuthorityMixin {
    }


    static class UserDeserializer extends JsonDeserializer<User> {

        private static final TypeReference<List<GrantedAuthority>> GRANTED_AUTHORITY_LIST = new TypeReference<>() {
        };

        @Override
        public User deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
            JsonNode root = mapper.readTree(jsonParser);
            User user = new User();
            String id = JsonNodeUtil.findNumberValue(root, "id");
            String username = JsonNodeUtil.findStringValue(root, "username");
            String password = JsonNodeUtil.findStringValue(root, "password");
            String nickname = JsonNodeUtil.findStringValue(root, "nickname");
            String note = JsonNodeUtil.findStringValue(root, "note");
            String tagline = JsonNodeUtil.findStringValue(root, "tagline");
            String gender = JsonNodeUtil.findStringValue(root, "gender");
            String avatar = JsonNodeUtil.findStringValue(root, "avatar");
            String email = JsonNodeUtil.findStringValue(root, "email");
            String phone = JsonNodeUtil.findStringValue(root, "phone");
            List<? extends GrantedAuthority> authorities = (List)mapper.readValue(this.readJsonNode(root, "authorities").traverse(mapper), GRANTED_AUTHORITY_LIST);

            user.setId(Long.parseLong(id));
            user.setUsername(username);
            user.setPassword(password);
            user.setNickname(nickname);
            user.setNote(note);
            user.setTagline(tagline);
            user.setGender(gender != null ? Gender.valueOf(gender) : null);
            user.setAvatar(avatar);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAuthorities(authorities);
            return user;
        }

        private JsonNode readJsonNode(JsonNode jsonNode, String field) {
            return (JsonNode)(jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance());
        }
    }

}
