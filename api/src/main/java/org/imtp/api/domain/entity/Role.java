package org.imtp.api.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;
import org.imtp.api.config.constant.CommonConstant;

/**
 * @Description
 * @Author ys
 * @Date 2023/7/18 11:25
 */

@Getter
@Setter
@TableName("im_role")
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

    /**
     * 是否为超级管理员角色
     * @return true 如果是超级管理员角色
     */
    public boolean isSuperAdmin() {
        return this.code.equals(CommonConstant.SUPER_ADMIN_ROLE_CODE);
    }

}
