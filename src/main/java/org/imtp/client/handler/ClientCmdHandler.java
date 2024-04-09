package org.imtp.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.imtp.enums.Command;
import org.imtp.enums.LoginState;
import org.imtp.packet.*;

import java.util.Scanner;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/8 14:53
 */
public class ClientCmdHandler extends SimpleChannelInboundHandler<Packet> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Packet packet = new LoginRequest("18855193274","136156");
        ctx.channel().writeAndFlush(packet);
        new Thread(){
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);
                while (scanner.hasNextLine()){
                    String s = scanner.nextLine();
                    ctx.channel().writeAndFlush(new TextMessage(s,18855193274L,1085385084L, Command.TEXT_MSG_REQ));
                }
            }
        }.start();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        switch (packet.getHeader().getCmd()){
            case LOGIN_REQ:
                break;
            case LOGIN_RES:
                LoginResponse loginResponse = (LoginResponse)packet;
                if(loginResponse.getLoginState().equals(LoginState.SUCCESS)){
                    System.out.println("登录成功");
                }else {
                    System.out.println("登录失败");
                }
                break;
            case TEXT_MSG_REQ :
                TextMessage textMessage = (TextMessage) packet;
                System.out.println(textMessage.getMessage());
                break;
            case TEXT_MSG_RES:
                DefaultMessageResponse response = (DefaultMessageResponse)packet;
                System.out.println(response.getState().name());
                break;
            default:
                throw new UnsupportedOperationException("不支持的操作");
        }
    }
}
