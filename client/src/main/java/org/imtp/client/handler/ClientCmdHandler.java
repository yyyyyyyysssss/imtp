package org.imtp.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.imtp.common.enums.Command;
import org.imtp.common.enums.LoginState;
import org.imtp.common.packet.*;

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
        CommandPacket commandPacket = (CommandPacket)packet;
        Header header = commandPacket.getHeader();
        Command cmd = header.getCmd();
        ByteBuf byteBuf = Unpooled.wrappedBuffer(commandPacket.getBytes());
        switch (cmd){
            case LOGIN_RES:
                LoginResponse loginResponse = new LoginResponse(byteBuf,header);
                if(loginResponse.getLoginState().equals(LoginState.SUCCESS)){
                    System.out.println("登录成功");
                }else {
                    System.out.println("登录失败");
                }
                break;
            case TEXT_MSG_REQ :
                TextMessage textMessage = new TextMessage(byteBuf,header);
                System.out.println(textMessage.getMessage());
                break;
            case TEXT_MSG_RES:
                DefaultMessageResponse response = new DefaultMessageResponse(byteBuf,header);
                System.out.println(response.getState().name());
                break;
            default:
                throw new UnsupportedOperationException("不支持的操作");
        }
    }
}
