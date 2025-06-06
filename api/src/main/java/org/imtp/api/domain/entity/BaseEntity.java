package org.imtp.api.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/17 11:30
 */
@Getter
@Setter
public class BaseEntity {

    @TableId(value = "id", type = IdType.INPUT)
    protected Long id;

    @TableField(value = "create_time",fill = FieldFill.INSERT)
    protected Date createTime;

    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    protected Date updateTime;

}
