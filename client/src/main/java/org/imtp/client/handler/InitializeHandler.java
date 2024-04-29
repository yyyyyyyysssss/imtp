package org.imtp.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.imtp.common.packet.Packet;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/29 16:36
 */
public class InitializeHandler extends SimpleChannelInboundHandler<Packet> {



    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {

    }
}
