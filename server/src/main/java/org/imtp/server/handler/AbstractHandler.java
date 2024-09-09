package org.imtp.server.handler;

import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import org.imtp.common.packet.base.Packet;
import org.imtp.server.config.RedisWrapper;
import org.imtp.server.service.ChatService;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/28 16:48
 */
public abstract class AbstractHandler<I> extends SimpleChannelInboundHandler<I> {

    @Resource
    protected ChatService chatService;

    @Resource
    protected RedisWrapper redisWrapper;


    protected List<String> fetchReceiverUserIdByPacket(Packet packet){
        List<String> receivers;
        if(packet.isGroup()){
            receivers = chatService.findUserIdByGroupId(packet.getReceiver());
        }else {
            receivers = List.of(packet.getReceiver().toString());
        }
        return receivers;
    }

}
