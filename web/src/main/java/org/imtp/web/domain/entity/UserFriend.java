package org.imtp.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/28 11:34
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_user_friend")
public class UserFriend {

    @TableId
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("friend_id")
    private Long friendId;

    @TableField("note")
    private String note;

    @TableField("note_pinyin")
    private String notePinyin;

    @TableField("create_time")
    private Date createTime;

}
