package org.imtp.web.domain.entity;

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
 * @Date 2023/7/18 13:27
 */

@Getter
@Setter
@TableName("im_user_role")
@Builder
public class UserRole {

    @Tolerate
    public UserRole(){
    }

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("role_id")
    private Long roleId;


}
