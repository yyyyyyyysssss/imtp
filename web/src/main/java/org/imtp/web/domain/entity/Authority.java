package org.imtp.web.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;
import org.imtp.web.enums.AuthorityType;
import org.imtp.web.mapper.TreeRelation;

import java.util.Date;

/**
 * @Description
 * @Author ys
 * @Date 2023/7/18 11:26
 */
@Getter
@Setter
@TableName("im_authority")
@Builder
public class Authority implements TreeRelation {

    @Tolerate
    public Authority(){
    }

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @TableField("parent_id")
    private Long parentId;

    @TableField("root_id")
    private Long rootId;

    @TableField("code")
    private String code;

    @TableField("name")
    private String name;

    @TableField("type")
    @EnumValue
    private AuthorityType type;

    @TableField("urls")
    private String urls;

    @TableField("icon")
    private String icon;

    @TableField("create_time")
    private Date createTime;

    @TableField("updated_time")
    private Date updatedTime;

    @Override
    public String parentFieldName() {
        return "parent_id";
    }

    @Override
    public String childFieldName() {
        return "id";
    }
}
