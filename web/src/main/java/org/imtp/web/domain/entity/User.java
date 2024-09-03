package org.imtp.web.domain.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.imtp.common.enums.Gender;

import java.util.Date;

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
public class User {

    @TableId
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("nickname")
    private String nickname;

    @TableField("gender")
    @EnumValue
    private Gender gender;

    @TableField("avatar")
    private String avatar;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;

    @TableField("create_time")
    private Date createTime;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender=" + gender +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
