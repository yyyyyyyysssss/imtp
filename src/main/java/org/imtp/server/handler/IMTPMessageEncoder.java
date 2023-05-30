package org.imtp.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.imtp.packet.Packet;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2023/4/10 14:11
 */
public class IMTPMessageEncoder extends MessageToMessageEncoder<Packet> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, List<Object> list) throws Exception {

    }
}
