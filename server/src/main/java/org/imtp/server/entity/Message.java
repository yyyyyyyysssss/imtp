package org.imtp.server.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.packet.base.Packet;
import org.imtp.server.idwork.IdGen;

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
@TableName("im_msg")
public class Message {


    public Message(Packet packet){
        this.id = IdGen.genId();
        this.senderUserId = packet.getSender();
        this.receiverUserId = packet.getReceiver();
        this.type = (int) packet.getHeader().getCmd().getCmdCode();
        this.sendTime = new Date();
        if(packet.isGroup()){
            this.deliveryMethod = DeliveryMethod.GROUP;
        }else {
            this.deliveryMethod = DeliveryMethod.SINGLE;
        }
    }

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

    @TableField("send_time")
    private Date sendTime;

    @TableField("delivery_method")
    @EnumValue
    private DeliveryMethod deliveryMethod;

}
