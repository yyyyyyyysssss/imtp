package org.imtp.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import org.imtp.server.enums.HistoryMsg;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/25 13:14
 */
@Getter
@Setter
@TableName("h_msg")
public class HistoryMessage {

    public HistoryMessage(){

    }

    public HistoryMessage(Long sender,Long receiver,Integer type,String msg){
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.status = HistoryMsg.WAIT_PUSH.getType();
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
    private Integer status;

    @TableField("msg")
    private String msg;

}
