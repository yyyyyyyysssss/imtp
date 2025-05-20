package org.imtp.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;
import org.imtp.api.enums.AuthorityType;
import org.imtp.api.mapper.TreeRelation;

import java.util.Date;
import java.util.Objects;

/**
 * @Description
 * @Author ys
 * @Date 2023/7/18 11:26
 */
@Getter
@Setter
@TableName("im_authority")
@Builder
public class Authority extends BaseEntity implements TreeRelation {

    @Tolerate
    public Authority(){
    }

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

    @TableField("route_path")
    private String routePath;

    @TableField("urls")
    private String urls;

    @TableField("icon")
    private String icon;

    @Override
    public String parentFieldName() {
        return "parent_id";
    }

    @Override
    public String childFieldName() {
        return "id";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Authority authority) {
            return this.getId().equals(authority.getId());
        }
        return false;
    }
}
