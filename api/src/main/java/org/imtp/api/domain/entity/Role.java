package org.imtp.api.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

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

}
