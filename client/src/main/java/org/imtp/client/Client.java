package org.imtp.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.enums.ClientType;
import org.imtp.client.handler.LoginHandler;
import org.imtp.common.codec.IMTPDecoder;
import org.imtp.common.codec.IMTPEncoder;
import org.imtp.client.component.ServiceInfo;
import org.imtp.common.packet.LoginRequest;
import org.imtp.common.packet.body.LoginInfo;

import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/8 14:39
 */
@Slf4j
public class Client implements Runnable {

    private String account;

    private String password;

    private EventLoopGroup group;

    private Bootstrap bootstrap;

    private ClientType clientType;

    private ChannelHandler channelHandler;

    public Client(String account, String password, ChannelHandler channelHandler) {
        this(account,password,channelHandler,ClientType.WINDOW);
    }

    public Client(String account, String password, ChannelHandler channelHandler, ClientType clientType) {
        this.account = account;
        this.password = password;
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
        Client client = new Client(account, password, new LoginHandler(),ClientType.CONSOLE);
        client.connect();
    }


    public void connect() {
        try {
            ServerAddress serverAddress = ServerAddressFactory.getServerAddress();
            ServiceInfo serviceInfo = serverAddress.serviceInfo();
            log.info("serviceInfo : {}",serviceInfo);
            ChannelFuture connected = bootstrap.connect(serviceInfo.getHost(), serviceInfo.getPort());
            connected.addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    log.info("与服务器建立连接成功");
                    Channel channel = channelFuture.channel();
                    //初始化上下文对象
                    if (ClientContextHolder.clientContext() == null){
                        ClientContextHolder.createClientContext(channel,this);
                        if (clientType.equals(ClientType.CONSOLE)) {
                            LoginInfo loginInfo = new LoginInfo(this.account, this.password);
                            channel.writeAndFlush(new LoginRequest(loginInfo));
                        }
                    }else {
                        LoginHandler loginHandler = channel.pipeline().get(LoginHandler.class);
                        loginHandler.setClientCmdHandlerHandler(this.channelHandler);
                        ClientContextHolder.clientContext().resetChannel(channel);
                        LoginInfo loginInfo = new LoginInfo(this.account, this.password);
                        channel.writeAndFlush(new LoginRequest(loginInfo));
                    }
                } else {
                    //每隔1秒重连
                    EventLoop eventLoop = channelFuture.channel().eventLoop();
                    eventLoop.schedule(() -> {
                        log.warn("与服务器建立连接失败,正在重新连接...");
                        connect();
                    },1L, TimeUnit.SECONDS);
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

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ClientType getClientType() {
        return clientType;
    }

}
