package org.imtp.server.handler;

import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.response.Result;
import org.imtp.server.config.RedisWrapper;
import org.imtp.server.feign.WebApi;
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
    protected WebApi webApi;

    @Resource
    protected RedisWrapper redisWrapper;


    protected List<String> fetchReceiverUserIdByPacket(Packet packet){
        List<String> receivers;
        if(packet.isGroup()){
            Result<List<String>> result = webApi.userIds(packet.getReceiver().toString());
            if (result.isSucceed()){
                receivers = result.getData();
            }else {
                receivers = List.of();
            }
        }else {
            receivers = List.of(packet.getReceiver().toString());
        }
        return receivers;
    }

}
