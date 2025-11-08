package org.imtp.api.domain.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import org.imtp.api.enums.MfaType;

@TableName("im_user_mfa")
@Getter
@Setter
public class UserMfa extends BaseEntity {

    @TableField("user_id")
    private Long userId;

    @TableField("username")
    private String username;

    @EnumValue
    private MfaType mfaType;

    @TableField("secret")
    private String secret;

    @TableField("enabled")
    private Boolean enabled;

}
