package org.imtp.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.handler.LoginHandler;
import org.imtp.common.codec.IMTPDecoder;
import org.imtp.common.codec.IMTPEncoder;
import org.imtp.common.packet.LoginRequest;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/8 14:39
 */
@Slf4j
public class Client {

    private String account;

    private String password;

    public Client(String account, String password) {
        this.account = account;
        this.password = password;
    }

    public static void main(String[] args) {
        int length;
        String account = null;
        String password = null,p = null;
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

        Client client = new Client(account,password);
        client.start();
    }


    public void start() {
        final EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new IMTPDecoder());
                            pipeline.addLast(new IMTPEncoder());
                            pipeline.addLast(new LoginHandler());
                        }
                    });
            ChannelFuture connected = bootstrap.connect("127.0.0.1", 2921);
            connected.addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    log.info("与服务器建立连接成功");
                    channelFuture.channel().writeAndFlush(new LoginRequest(this.account,this.password));
                } else {
                    log.warn("与服务器建立连接失败");
                }
            });
            connected.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            group.shutdownGracefully();
        }
    }

}
