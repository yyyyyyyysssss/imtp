package org.imtp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.codec.IMTPDecoder;
import org.imtp.common.codec.IMTPEncoder;
import org.imtp.common.utils.JsonUtil;
import org.imtp.server.config.ServiceRegister;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.enums.Model;
import org.imtp.server.handler.LoginHandler;
import org.imtp.server.utils.SpringUtil;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/28 9:34
 */
@Component
@Slf4j
public class IMServer{

    @Resource
    private LoginHandler loginHandler;

    @Resource
    private ServerProperties serverProperties;

    private EventLoopGroup bossEventLoopGroup;
    private EventLoopGroup workEventLoopGroup;
    private ChannelFuture channelFuture;

    @PostConstruct
    public void start() {
        bossEventLoopGroup=new NioEventLoopGroup(2);
        workEventLoopGroup=new NioEventLoopGroup(8);
        try {
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            serverBootstrap.group(bossEventLoopGroup,workEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024) //请求线程满时，用于临时存放完成三次握手的请求队列大小
                    .option(ChannelOption.SO_KEEPALIVE,true) // 长连接
                    .option(ChannelOption.TCP_NODELAY,true)  //数据立即发送，不会等待缓冲区填满或延迟定时器到期
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new IMTPDecoder());
                            pipeline.addLast(new IMTPEncoder());
                            pipeline.addLast(loginHandler);
                        }
                    });
            channelFuture = serverBootstrap.bind(serverProperties.getHost(), serverProperties.getPort()).sync();
            channelFuture.addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()){
                    log.info("IMServer started");
                    //初始化上下文对象
                    ChannelContextHolder.createChannelContext();

                    //集群模式将服务器信息注册
                    if (Model.CLUSTER.equals(serverProperties.getConfiguration().getModel())){
                        String id = serverProperties.getConfiguration().getId();
                        if (id == null){
                            id = UUID.randomUUID().toString().replace("-","");
                            serverProperties.getConfiguration().setId(id);
                        }
                        ServiceRegister serviceRegister = SpringUtil.getBean(ServiceRegister.class);
                        if (serviceRegister == null){
                            throw new NullPointerException("serviceRegister is null");
                        }
                        serviceRegister.register(id,JsonUtil.toJSONString(serverProperties));
                    }

                }
            });
        }catch (Exception e){
            log.error("IMServer: ",e);
            stop();
        }
    }


    @PreDestroy
    public void stop(){
        log.info("Stopping IMServer");
        try {
            bossEventLoopGroup.shutdownGracefully().sync();
            workEventLoopGroup.shutdownGracefully().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Stop IMServer error: ",e);
        }
    }

}
