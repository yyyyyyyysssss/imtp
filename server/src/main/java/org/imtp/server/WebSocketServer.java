package org.imtp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.server.handler.AuthenticationHandler;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/7 20:03
 */
@Component
@Slf4j
public class WebSocketServer implements SmartLifecycle {

    private EventLoopGroup bossEventLoopGroup;

    private EventLoopGroup workEventLoopGroup;

    private ChannelFuture channelFuture;

    @Resource
    private ServerProperties serverProperties;

    @Resource
    private AuthenticationHandler authenticationHandler;

    private boolean isRunning = false;

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
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //http请求和响应的编解码
                            pipeline.addLast(new HttpServerCodec());
                            //http消息合并
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            //数据压缩
                            pipeline.addLast(new WebSocketServerCompressionHandler());
                            //协议升级
                            pipeline.addLast(new WebSocketServerProtocolHandler("/im",null,true));
                            //身份认证
                            pipeline.addLast(authenticationHandler);
                            //心跳检测
                            pipeline.addLast(new IdleStateHandler(60,60,120));
                        }
                    });
            channelFuture = serverBootstrap.bind(serverProperties.getWebsocket().getHost(), serverProperties.getWebsocket().getPort()).sync();
            channelFuture.addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()){
                    log.info("WebSocket Server started");
                    isRunning = true;
                }else {
                    log.info("Failed to start WebSocket Server");
                    isRunning = false;
                }
            });
        }catch (Exception e){
            log.error("WebSocket Server: ",e);stop();
            stop();
        }
    }

    @Override
    public void stop(){
        log.info("Stopping WebSocket Server");
        try {
            bossEventLoopGroup.shutdownGracefully().sync();
            workEventLoopGroup.shutdownGracefully().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Stop WebSocket Server Error: ",e);
        }
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPhase() {
        return 1;
    }
}
