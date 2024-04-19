package org.imtp.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.imtp.common.enums.LoginState;
import org.imtp.common.packet.DefaultMessageResponse;
import org.imtp.common.packet.LoginResponse;
import org.imtp.common.packet.Packet;
import org.imtp.common.packet.TextMessage;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/8 14:53
 */
public class ClientCmdHandler extends SimpleChannelInboundHandler<Packet> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

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
