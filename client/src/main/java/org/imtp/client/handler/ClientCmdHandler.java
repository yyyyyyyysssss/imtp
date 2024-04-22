package org.imtp.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.context.ClientContext;
import org.imtp.common.enums.Command;
import org.imtp.common.enums.LoginState;
import org.imtp.common.packet.*;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/8 14:53
 */
@Slf4j
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
                    log.info("登录成功");
                    ClientContext.setUser(loginResponse.getReceiver() + "");
                }else {
                    log.info("登录失败");
                }
                break;
            case PRIVATE_MSG :
                PrivateChatMessage privateChatMessage = new PrivateChatMessage(byteBuf,header);
                System.out.println("用户["+ privateChatMessage.getSender() + "]:" + privateChatMessage.getMessage());
                break;
            case GROUP_CHAT_MSG:
                GroupChatMessage groupChatMessage = new GroupChatMessage(byteBuf,header);
                System.out.println("用户["+ groupChatMessage.getSender() + "]:" + groupChatMessage.getMessage());
                break;
            case MSG_RES:
                DefaultMessageResponse response = new DefaultMessageResponse(byteBuf,header);
                break;
            default:
                throw new UnsupportedOperationException("不支持的操作");
        }
    }
}
