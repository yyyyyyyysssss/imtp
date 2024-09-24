package org.imtp.web.domain.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.packet.MessageMetadata;

import java.util.Date;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/28 11:29
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "im_msg",autoResultMap = true)
public class Message {

    @TableId
    private Long id;

    @TableField("sender_user_id")
    private Long senderUserId;

    @TableField("receiver_user_id")
    private Long receiverUserId;

    @TableField("type")
    private Integer type;

    @TableField("content")
    private String content;

    @TableField(value = "content_metadata",typeHandler = JacksonTypeHandler.class)
    private MessageMetadata contentMetadata;

    @TableField("send_time")
    private Date sendTime;

    @TableField("delivery_method")
    @EnumValue
    private DeliveryMethod deliveryMethod;

}
