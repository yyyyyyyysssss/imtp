package org.imtp.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_group")
public class Group {

    @TableId
    private Long id;

    @TableField("name")
    private String name;

    @TableField("create_user_id")
    private Long createUserId;

    @TableField("avatar")
    private String avatar;

    @TableField("create_time")
    private Date createTime;

    @TableField("state")
    private Boolean state;

}
