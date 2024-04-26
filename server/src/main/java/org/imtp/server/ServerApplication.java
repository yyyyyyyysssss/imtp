package org.imtp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.imtp.common.codec.IMTPDecoder;
import org.imtp.common.codec.IMTPEncoder;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.handler.CommandHandler;
import org.imtp.server.service.H2DBChatService;
import org.imtp.server.storage.SqlHandler;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @Description
 * @Author ys
 * @Date 2023/4/10 11:44
 */
@SpringBootApplication
@EnableAsync
public class ServerApplication implements ApplicationRunner {



    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class);
    }

    public void start(){
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
                            pipeline.addLast(new CommandHandler(new H2DBChatService(new SqlHandler())));
                        }
                    });
            ChannelFuture cf = serverBootstrap.bind("127.0.0.1", 2921).sync();
            cf.addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()){
                    System.out.println("ServerApplication started");
                    //初始化上下文对象
                    ChannelContextHolder.createChannelContext();
                }
            });
            cf.channel().closeFuture().sync();
        }catch (Exception e){
            bossEventLoopGroup.shutdownGracefully();
            workEventLoopGroup.shutdownGracefully();
        }finally {
            bossEventLoopGroup.shutdownGracefully();
            workEventLoopGroup.shutdownGracefully();
        }
    }

    @Async
    @Override
    public void run(ApplicationArguments args) {
        start();
    }
}
