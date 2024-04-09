package org.imtp.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.imtp.packet.Packet;

/**
 * @Description 编码器
 * @Author ys
 * @Date 2024/4/7 15:35
 */
public class IMTPEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) {
        packet.encodeAsByteBuf(byteBuf);
    }
}
