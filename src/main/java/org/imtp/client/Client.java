package org.imtp.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.imtp.client.handler.ClientCmdHandler;
import org.imtp.codec.IMTPDecoder;
import org.imtp.codec.IMTPEncoder;
import org.imtp.enums.Command;
import org.imtp.packet.TextMessage;

import java.util.Scanner;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/8 14:39
 */
public class Client {

    public static void main(String[] args) {
        final EventLoopGroup group=new NioEventLoopGroup(1);
        Bootstrap bootstrap=new Bootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new IMTPDecoder());
                            pipeline.addLast(new IMTPEncoder());
                            pipeline.addLast(new ClientCmdHandler());
                        }
                    });
            ChannelFuture connected = bootstrap.connect("127.0.0.1", 2921);
            connected.addListener((ChannelFutureListener) channelFuture -> {
                if(channelFuture.isSuccess()){
                    System.out.println("连接成功");
                }else {
                    System.out.println("连接失败");
                }
            });
            connected.channel().closeFuture().sync();
        }catch (Exception e){
            System.out.println(e.getMessage());
        } finally {
            group.shutdownGracefully();
        }
    }

}
