package org.imtp.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.imtp.client.handler.ClientCmdHandler;
import org.imtp.common.codec.IMTPDecoder;
import org.imtp.common.codec.IMTPEncoder;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.*;

import java.util.Scanner;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/8 14:39
 */
public class Client {

    private Long account;

    private Long receiver;

    private boolean isGroupChat;

    public Client(Long account,Long receiver,boolean isGroupChat){
        this.account = account;
        this.receiver = receiver;
        this.isGroupChat = isGroupChat;
    }

    public static void main(String[] args) {
        int length;
        Long account = null,receiver = null;
        boolean isGroupChat = false;
        String p;
        char c;
        char[] cc;
        if(args != null && (length = args.length) > 0){
            for (int i = 0; i < length; i++) {
                p = args[i];
                if((cc = p.toCharArray())[0] != '-'){
                    throw new RuntimeException("参数错误! 参数必须以-开头");
                }
                switch ((c = cc[1])){
                    case 'u':
                        account = Long.parseLong(args[++i]);
                        break;
                    case 's':
                        receiver = Long.parseLong(args[++i]);
                        break;
                    case 'g':
                        isGroupChat = true;
                        break;
                    default:
                        throw new UnsupportedOperationException("不支持的操作: -" + c);
                }

            }
        }

        Client client = new Client(account, receiver,isGroupChat);
        client.start();
    }


    public void start(){
        final EventLoopGroup group=new NioEventLoopGroup(1);
        Bootstrap bootstrap=new Bootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new IMTPDecoder());
                            pipeline.addLast(new IMTPEncoder());
                            pipeline.addLast(new ClientCmdHandler());
                        }
                    });
            ChannelFuture connected = bootstrap.connect("127.0.0.1", 2921);
            connected.addListener((ChannelFutureListener) channelFuture -> {
                if(channelFuture.isSuccess()){
                    Packet packet = new LoginRequest(this.account.toString(),"123456");
                    channelFuture.channel().writeAndFlush(packet);
                    new Thread(() -> {
                        Scanner scanner = new Scanner(System.in);
                        while (scanner.hasNextLine()){
                            String s = scanner.nextLine();
                            if(isGroupChat){
                                channelFuture.channel().writeAndFlush(new GroupChatMessage(s,this.account,this.receiver));
                            }else {
                                channelFuture.channel().writeAndFlush(new PrivateChatMessage(s,this.account,this.receiver));
                            }
                        }
                    }).start();
                }else {

                    System.out.println("连接失败");
                }
            });
            connected.channel().closeFuture().sync();
        }catch (Exception e){
            System.out.println(e.getMessage());
        } finally {
            group.shutdownGracefully();
        }
    }

}
