package org.imtp.server.handler;

import io.netty.channel.SimpleChannelInboundHandler;
import org.imtp.common.packet.Packet;
import org.imtp.server.entity.HistoryMessage;
import org.imtp.server.enums.HistoryMsg;
import org.imtp.server.service.ChatService;


/**
 * @Description
 * @Author ys
 * @Date 2024/4/26 10:16
 */
public abstract class AbstractHandler<T extends Packet> extends SimpleChannelInboundHandler<T> {

    protected ChatService chatService;

    public AbstractHandler(ChatService chatService){
        this.chatService = chatService;
    }

    protected void saveHistoryMsg(Packet packet, HistoryMsg historyMsg,String msg){
        if(packet.getSender().equals(packet.getReceiver())){
            return;
        }
        HistoryMessage historyMessage = new HistoryMessage(packet.getSender(),packet.getReceiver(),historyMsg.getType(),msg);
        chatService.saveHistoryMessage(historyMessage);
    }


}
