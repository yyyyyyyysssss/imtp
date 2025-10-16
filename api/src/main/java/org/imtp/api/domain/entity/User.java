package org.imtp.api.domain.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableField;
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
import org.imtp.api.utils.JsonNodeUtils;
import org.imtp.common.enums.Gender;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
public class User extends BaseEntity implements UserDetails, CredentialsContainer {

    @TableField(exist = false)
    private List<? extends GrantedAuthority> authorities;

    @TableField(exist = false)
    private String tokenId;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("nickname")
    private String nickname;

    @TableField("nickname_pinyin")
    private String nicknamePinyin;

    @TableField("tagline")
    private String tagline;

    @TableField("enabled")
    private boolean enabled;

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

    @Override
    public String toString() {
        return "User{" +
                "authorities=" + authorities +
                ", id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", nicknamePinyin='" + nicknamePinyin + '\'' +
                ", tagline='" + tagline + '\'' +
                ", gender=" + gender +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", region='" + region + '\'' +
                ", createTime=" + this.createTime +
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
    public abstract static class UserMixin {
    }


    static class UserDeserializer extends JsonDeserializer<User> {

        private static final TypeReference<List<GrantedAuthority>> GRANTED_AUTHORITY_LIST = new TypeReference<>() {
        };

        @Override
        public User deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
            JsonNode root = mapper.readTree(jsonParser);
            User user = new User();
            String id = JsonNodeUtils.findNumberValue(root, "id");
            String username = JsonNodeUtils.findStringValue(root, "username");
            String password = JsonNodeUtils.findStringValue(root, "password");
            String nickname = JsonNodeUtils.findStringValue(root, "nickname");
            String nicknamePinyin = JsonNodeUtils.findStringValue(root, "nicknamePinyin");
            String tagline = JsonNodeUtils.findStringValue(root, "tagline");
            String gender = JsonNodeUtils.findStringValue(root, "gender");
            String avatar = JsonNodeUtils.findStringValue(root, "avatar");
            String email = JsonNodeUtils.findStringValue(root, "email");
            String phone = JsonNodeUtils.findStringValue(root, "phone");
            List<? extends GrantedAuthority> authorities = (List)mapper.readValue(this.readJsonNode(root, "authorities").traverse(mapper), GRANTED_AUTHORITY_LIST);
            String tokenId = JsonNodeUtils.findStringValue(root, "tokenId");

            user.setId(Long.parseLong(id));
            user.setUsername(username);
            user.setPassword(password);
            user.setNickname(nickname);
            user.setNicknamePinyin(nicknamePinyin);
            user.setTagline(tagline);
            user.setGender(gender != null ? Gender.valueOf(gender) : null);
            user.setAvatar(avatar);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAuthorities(authorities);
            user.setTokenId(tokenId);
            return user;
        }

        private JsonNode readJsonNode(JsonNode jsonNode, String field) {
            return (JsonNode)(jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance());
        }
    }

}
