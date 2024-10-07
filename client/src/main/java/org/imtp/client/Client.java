package org.imtp.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.component.ServiceInfo;
import org.imtp.client.constant.ConnectListener;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.enums.ClientType;
import org.imtp.client.handler.AuthenticationHandler;
import org.imtp.common.codec.IMTPDecoder;
import org.imtp.common.codec.IMTPEncoder;
import org.imtp.common.packet.AuthenticationRequest;
import org.imtp.common.packet.body.TokenInfo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/8 14:39
 */
@Slf4j
public class Client implements Runnable {

    private TokenInfo tokenInfo;

    private EventLoopGroup group;

    private Bootstrap bootstrap;

    private ClientType clientType;

    private ChannelHandler channelHandler;

    private ConnectListener connectListener;

    private ServerAddress serverAddress;

    private Config config;

    public Client(ChannelHandler channelHandler, TokenInfo tokenInfo) {
        this(tokenInfo,channelHandler,ClientType.WINDOW);
    }

    public Client(TokenInfo tokenInfo, ChannelHandler channelHandler, ClientType clientType) {
        this.tokenInfo = tokenInfo;
        this.clientType = clientType;
        this.channelHandler = channelHandler;
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new IMTPDecoder());
                        pipeline.addLast(new IMTPEncoder());
                        pipeline.addLast(channelHandler);
                    }
                });
        this.config = Config.getInstance();
        this.serverAddress = ServerAddressFactory.getServerAddress(config.getModel());
    }

    public void addListener(ConnectListener connectListener){
        this.connectListener = connectListener;
    }

    public static void main(String[] args) {
        int length;
        String account = null;
        String password = null, p = null;
        char c;
        char[] cc;
        if (args != null && (length = args.length) > 0) {
            for (int i = 0; i < length; i++) {
                p = args[i];
                if ((cc = p.toCharArray())[0] != '-') {
                    throw new RuntimeException("参数错误! 参数必须以-开头");
                }
                switch ((c = cc[1])) {
                    case 'u':
                        account = args[++i];
                        break;
                    case 'p':
                        password = args[++i];
                        break;
                    default:
                        throw new UnsupportedOperationException("不支持的操作: -" + c);
                }

            }
        }
        if (account == null || password == null) {
            System.out.println("账号或密码不能为空");
            System.exit(0);
        }
        Client client = new Client(null, new AuthenticationHandler(),ClientType.CONSOLE);
        client.connect();
    }

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public void connect() {
        try {
            ServiceInfo serviceInfo = serverAddress.serviceInfo();
            if (serviceInfo == null){
                log.warn("获取服务器信息失败...");
                scheduledExecutorService.schedule(() -> {
                    log.warn("正在重新获取服务器信息...");
                    connect();
                },1,TimeUnit.SECONDS);
                return;
            }
            log.info("服务器信息: {}",serviceInfo);
            ChannelFuture connected = bootstrap.connect(serviceInfo.getHost(), serviceInfo.getPort());
            connected.addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    log.info("与服务器建立连接成功");
                    Channel channel = channelFuture.channel();
                    //初始化上下文对象
                    if (ClientContextHolder.clientContext() == null){
                        ClientContextHolder.createClientContext(channel,this,tokenInfo);
                        if (connectListener != null){
                            connectListener.connected();
                        }
                    }else {
                        AuthenticationHandler authenticationHandler = channel.pipeline().get(AuthenticationHandler.class);
                        authenticationHandler.setClientCmdHandlerHandler(this.channelHandler);
                        ClientContextHolder.clientContext().resetChannel(channel);
                        channel.writeAndFlush(new AuthenticationRequest(this.tokenInfo.getAccessToken()));
                    }
                } else {
                    //每隔2秒重连
                    EventLoop eventLoop = channelFuture.channel().eventLoop();
                    eventLoop.schedule(() -> {
                        log.warn("与服务器建立连接失败,正在重新连接...");
                        connect();
                    },2L, TimeUnit.SECONDS);
                }
            });
            connected.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("error:", e);
            group.shutdownGracefully();
        }
    }

    @Override
    public void run() {
        connect();
    }

    public void resetChannelHandler(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    public ClientType getClientType() {
        return clientType;
    }

}
