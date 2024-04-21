package org.imtp.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.*;

import java.util.List;

/**
 * @Description 解码器
 * @Author ys
 * @Date 2024/4/7 15:35
 */
public class IMTPDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        //如果可读字节小于协议头字节数则不读
        if(byteBuf.readableBytes() < Header.headLength()){
            return;
        }
        //标记当前读取位置
        byteBuf.markReaderIndex();
        //解码消息头
        Header header = new Header(byteBuf);
        //获取消息体长度
        int length = header.getLength();
        //如果剩余可读字节小于消息体的长度则重置读指针并等待下次重新读取
        if(byteBuf.readableBytes() < length){
            byteBuf.resetReaderIndex();
            return;
        }
        Packet packet = new CommandPacket(header,byteBuf);
        list.add(packet);
    }
}
