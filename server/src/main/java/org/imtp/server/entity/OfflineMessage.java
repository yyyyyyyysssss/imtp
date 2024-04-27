package org.imtp.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.imtp.common.packet.Packet;
import org.imtp.server.enums.OfflineMsgEnum;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/25 13:14
 */
@Getter
@Setter
@Builder
@TableName("o_msg")
public class OfflineMessage {


    public OfflineMessage(){

    }

    public OfflineMessage(Packet packet, OfflineMsgEnum type, String msg){
        this.sender = packet.getSender();
        this.receiver = packet.getReceiver();
        this.type = type.getType();
        this.timestamp = System.currentTimeMillis();
        this.status = OfflineMsgEnum.WAIT_PUSH.getType();
        this.msg = msg;
    }

    @TableId
    private Long id;

    @TableField("sender")
    private Long sender;

    @TableField("receiver")
    private Long receiver;

    @TableField("timestamp")
    private Long timestamp;

    @TableField("type")
    private Integer type;

    @TableField("status")
    private Integer status = OfflineMsgEnum.WAIT_PUSH.getType();

    @TableField("msg")
    private String msg;

}
