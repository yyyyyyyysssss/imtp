package org.imtp.server.handler;

import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import org.imtp.server.service.ChatService;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/28 16:48
 */
public abstract class AbstractHandler<I> extends SimpleChannelInboundHandler<I> {

    @Resource
    protected ChatService chatService;

}
