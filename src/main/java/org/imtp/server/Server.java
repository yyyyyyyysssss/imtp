package org.imtp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.imtp.codec.IMTPDecoder;
import org.imtp.codec.IMTPEncoder;
import org.imtp.server.handler.InitializeHandler;

/**
 * @Description
 * @Author ys
 * @Date 2023/4/10 11:44
 */
public class Server {

    public static void main(String[] args) {
        final EventLoopGroup bossEventLoopGroup=new NioEventLoopGroup(2);
        final EventLoopGroup workEventLoopGroup=new NioEventLoopGroup(8);
        try {
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            serverBootstrap.group(bossEventLoopGroup,workEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new IMTPDecoder());
                            pipeline.addLast(new IMTPEncoder());
                            pipeline.addLast(new InitializeHandler());
                        }
                    });
            ChannelFuture cf = serverBootstrap.bind("127.0.0.1", 2921).sync();
            cf.addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()){
                    System.out.println("服务器启动成功");
                }
            });
            cf.channel().closeFuture().sync();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            bossEventLoopGroup.shutdownGracefully();
            workEventLoopGroup.shutdownGracefully();
        }
    }

}
