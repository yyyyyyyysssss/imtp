package org.imtp.server.entity;

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

    private Long id;

    private Long sender;

    private Long receiver;

    private Long timestamp;

    private Integer type;

    private Integer status;

    private String msg;

}
