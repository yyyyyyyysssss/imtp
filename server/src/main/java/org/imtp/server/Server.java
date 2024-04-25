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
import org.imtp.server.service.ChatService;
import org.imtp.server.storage.SqlHandler;

/**
 * @Description
 * @Author ys
 * @Date 2023/4/10 11:44
 */
public class Server {

    private ChatService chatService;

    public Server(ChatService chatService){
        this.chatService = chatService;
    }

    public static void main(String[] args) {
        SqlHandler sqlHandler = new SqlHandler();
        ChatService chatService = new H2DBChatService(sqlHandler);
        Server server = new Server(chatService);
        server.start();
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
                            pipeline.addLast(new CommandHandler(chatService));
                        }
                    });
            ChannelFuture cf = serverBootstrap.bind("127.0.0.1", 2921).sync();
            cf.addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()){
                    System.out.println("Server started");
                    //初始化上下文对象
                    ChannelContextHolder.createChannelContext();
                }
            });
            cf.channel().closeFuture().sync();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            bossEventLoopGroup.shutdownGracefully();
            workEventLoopGroup.shutdownGracefully();
        }
    }

}
