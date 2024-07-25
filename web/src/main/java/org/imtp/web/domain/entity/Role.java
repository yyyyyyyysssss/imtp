package org.imtp.web.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

import java.util.Date;

/**
 * @Description
 * @Author ys
 * @Date 2023/7/18 11:25
 */

@Getter
@Setter
@TableName("im_role")
@Builder
public class Role {

    @Tolerate
    public Role(){
    }

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @TableField("code")
    private String code;

    @TableField("name")
    private String name;

    @TableField("create_time")
    private Date createTime;

}
