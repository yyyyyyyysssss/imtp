package org.imtp.api.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

/**
 * @Description
 * @Author ys
 * @Date 2023/7/18 13:29
 */
@Getter
@Setter
@TableName("im_role_authority")
@Builder
public class RoleAuthority {

    @Tolerate
    public RoleAuthority(){
    }

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("role_id")
    private Long roleId;

    @TableField("authority_id")
    private Long authorityId;


}
