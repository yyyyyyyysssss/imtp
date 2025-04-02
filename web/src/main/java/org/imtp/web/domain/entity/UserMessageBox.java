package org.imtp.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.imtp.web.enums.MessageBoxType;

import java.util.Date;

/**
 * @Description
 * @Author ys
 * @Date 2024/12/13 13:11
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_user_msg_box")
public class UserMessageBox {

    @TableId
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("session_id")
    private Long sessionId;

    @TableField("msg_id")
    private Long msgId;

    @TableField("box_type")
    private MessageBoxType boxType;
}
