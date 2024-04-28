package org.imtp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.codec.IMTPDecoder;
import org.imtp.common.codec.IMTPEncoder;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.handler.CommandHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/28 9:34
 */
@Component
@Slf4j
public class IMServer implements CommandLineRunner {

    @Resource
    private CommandHandler commandHandler;

    private final EventLoopGroup bossEventLoopGroup=new NioEventLoopGroup(2);
    private final EventLoopGroup workEventLoopGroup=new NioEventLoopGroup(8);

    @Async
    @Override
    public void run(String... args) {
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
                            pipeline.addLast(commandHandler);
                        }
                    });
            ChannelFuture cf = serverBootstrap.bind("127.0.0.1", 2921).sync();
            cf.addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()){
                    log.info("IMServer started");
                    //初始化上下文对象
                    ChannelContextHolder.createChannelContext();
                }
            });
            cf.channel().closeFuture().sync();
        }catch (Exception e){
            log.error("IMServer: ",e);
        }finally {
            shutdown();
        }
    }


    @PreDestroy
    public void shutdown(){
        log.info("IMServer is shutting down");
        bossEventLoopGroup.shutdownGracefully();
        workEventLoopGroup.shutdownGracefully();
    }

}
