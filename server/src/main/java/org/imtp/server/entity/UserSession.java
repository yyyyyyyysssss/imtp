package org.imtp.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/16 16:08
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_user")
public class UserSession {

    @TableField
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("receiver_user_id")
    private Long receiverUserId;

    @TableField("last_msg_id")
    private Long lastMsgId;

}
