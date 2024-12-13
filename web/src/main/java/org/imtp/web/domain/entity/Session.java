package org.imtp.web.domain.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.imtp.common.enums.DeliveryMethod;

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
@TableName("im_session")
public class Session {

    @TableId
    private Long id;

    @TableField("sender_user_id")
    private Long senderUserId;

    @TableField("receiver_user_id")
    private Long receiverUserId;

    @TableField("last_msg_id")
    private Long lastMsgId;

    @TableField("delivery_method")
    @EnumValue
    private DeliveryMethod deliveryMethod;

}
