package org.imtp.api.domain.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;
import org.imtp.api.enums.AuthorityType;
import org.imtp.api.mapper.TreeRelation;

import java.util.List;
import java.util.Objects;

/**
 * @Description
 * @Author ys
 * @Date 2023/7/18 11:26
 */
@Getter
@Setter
@TableName(value = "im_authority",autoResultMap = true)
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

    @TableField(value = "route_path", updateStrategy = FieldStrategy.ALWAYS)
    private String routePath;

    @TableField(value = "urls", typeHandler = JacksonTypeHandler.class, updateStrategy = FieldStrategy.ALWAYS)
    private List<AuthorityUrl> urls;

    @TableField("icon")
    private String icon;

    @TableField("sort")
    private Integer sort;

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
