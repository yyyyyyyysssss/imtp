package org.imtp.api.domain.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Tolerate;

@Getter
@Setter
@Builder
@TableName("im_dict")
public class Dictionary extends BaseEntity {

    @Tolerate
    public Dictionary(){
    }

    @TableField("code")
    private String code;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("enabled")
    private Boolean enabled;

    @TableField("sort")
    private Integer sort;

    @TableField("ext_json")
    private String extJson;

}
