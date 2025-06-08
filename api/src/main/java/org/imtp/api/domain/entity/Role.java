package org.imtp.api.domain.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;
import org.imtp.api.enums.RoleType;

/**
 * @Description
 * @Author ys
 * @Date 2023/7/18 11:25
 */

@Getter
@Setter
@TableName(value = "im_role",autoResultMap = true)
@Builder
public class Role extends BaseEntity{

    @Tolerate
    public Role(){
    }

    @TableField("code")
    private String code;

    @TableField("name")
    private String name;

    @TableField("enabled")
    private Boolean enabled;

    @TableField("type")
    @EnumValue
    private RoleType type;

    /**
     * 是否为超级管理员角色
     * @return true 如果是超级管理员角色
     */
    public boolean isSuperAdmin() {
        return this.type.equals(RoleType.SUPER_ADMIN);
    }

}
