package org.imtp.app;

import android.util.Log;

import org.imtp.app.context.ClientContextHolder;
import org.imtp.app.handler.AuthenticationHandler;
import org.imtp.app.model.MessageModel;
import org.imtp.common.codec.IMTPDecoder;
import org.imtp.common.codec.IMTPEncoder;
import org.imtp.common.packet.body.TokenInfo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient implements Runnable {

    private static final String TAG = "NettyClient";

    private final EventLoopGroup eventLoopGroup;

    private final Bootstrap bootstrap;

    private AppConfig appConfig;

    private TokenInfo tokenInfo;

    private MessageModel messageModel;

    private ConnectListener connectListener;

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public NettyClient(TokenInfo tokenInfo, MessageModel messageModel) {
        this.tokenInfo = tokenInfo;
        this.messageModel = messageModel;
        this.appConfig = AppConfig.getInstance();
        this.eventLoopGroup = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        NettyClient that = this;
        this.bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IMTPDecoder());
                        pipeline.addLast(new IMTPEncoder());
                        pipeline.addLast(new AuthenticationHandler(that,messageModel));
                    }
                });
    }

    public void setTokenInfo(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public void connect() {
        try {
            ChannelFuture connect = this.bootstrap.connect("10.0.2.2", 2921);
            connect.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    Log.i(TAG, "server connection success");
                    //初始化上下文对象
                    ClientContextHolder.createClientContext(future.channel(), tokenInfo);
                    if (this.connectListener != null) {
                        this.connectListener.connected();
                    }
                } else {
                    if (future.cause() != null) {
                        Throwable throwable = future.cause();
                        Log.w(TAG, "server connection exception: " + throwable.getMessage());
                        if (this.connectListener != null) {
                            this.connectListener.exception(future.cause());
                        }
                    }
                    future.channel().eventLoop().schedule(() -> {
                        Log.w(TAG, "server reconnecting...");
                        connect();
                    }, 2L, TimeUnit.SECONDS);
                }

            });
            connect.channel().closeFuture().sync();
        } catch (Exception e) {
            Log.e(TAG, "connect error: " + e.getMessage());
            this.eventLoopGroup.shutdownGracefully();
        }

    }

    @Override
    public void run() {
        connect();
    }

    public void addListener(ConnectListener connectListener) {
        this.connectListener = connectListener;
    }

    public void stop() {
        Log.i(TAG, "Stopping NettyClient");
        this.eventLoopGroup.shutdownGracefully();
    }

}
