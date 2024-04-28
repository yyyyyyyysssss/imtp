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

    @TableField("createUserId")
    private Long create_user_id;

    @TableField("createTime")
    private Date create_time;

    @TableField("state")
    private Boolean state;

}
