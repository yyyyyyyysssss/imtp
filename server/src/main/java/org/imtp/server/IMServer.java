package org.imtp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.codec.IMTPDecoder;
import org.imtp.common.codec.IMTPEncoder;
import org.imtp.common.enums.ServerModel;
import org.imtp.common.utils.JsonUtil;
import org.imtp.server.config.ServiceRegister;
import org.imtp.server.handler.AuthenticationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/28 9:34
 */
@Component
@Slf4j
public class IMServer implements SmartLifecycle {

    @Resource
    private AuthenticationHandler authenticationHandler;

    @Resource
    private ServerProperties serverProperties;

    private EventLoopGroup bossEventLoopGroup;
    private EventLoopGroup workEventLoopGroup;
    private ChannelFuture channelFuture;

    private boolean isRunning = false;

    private ServiceRegister serviceRegister;
    @Autowired(required = false)
    public void setServiceRegister(ServiceRegister serviceRegister) {
        this.serviceRegister = serviceRegister;
    }

    @Override
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
                            //解码
                            pipeline.addLast(new IMTPDecoder());
                            //编码
                            pipeline.addLast(new IMTPEncoder());
                            //身份认证
                            pipeline.addLast(authenticationHandler);
                            //心跳检测
                            pipeline.addLast(new IdleStateHandler(120,60,0));
                        }
                    });
            channelFuture = serverBootstrap.bind(serverProperties.getHost(), serverProperties.getPort()).sync();
            channelFuture.addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()){
                    isRunning = true;
                    log.info("IMServer started on port {}",serverProperties.getPort());
                    //集群模式将服务器信息注册
                    if (ServerModel.CLUSTER.equals(serverProperties.getConfiguration().getModel())){
                        String id = serverProperties.getConfiguration().getId();
                        if (id == null){
                            id = UUID.randomUUID().toString().replace("-","");
                            serverProperties.getConfiguration().setId(id);
                        }
                        serviceRegister.register(id,JsonUtil.toJSONString(serverProperties));
                    }
                }else {
                    isRunning = false;
                }
            });
        }catch (Exception e){
            log.error("IMServer: ",e);
            stop();
        }
    }

    @Override
    public void stop(){
        log.info("Stopping IMServer");
        isRunning = false;
        try {
            bossEventLoopGroup.shutdownGracefully().sync();
            workEventLoopGroup.shutdownGracefully().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Stop IMServer error: ",e);
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPhase() {
        return 0;
    }

}
