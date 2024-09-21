package org.imtp.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.context.DefaultClientUserChannelContext;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.AuthenticationResponse;
import org.imtp.common.packet.CommandPacket;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.packet.body.UserInfo;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/21 18:55
 */
@Slf4j
@ChannelHandler.Sharable
public class AuthenticationHandler extends AbstractMessageModelHandler<Packet>{

    private ClientCmdHandlerHandler clientCmdHandlerHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet msg) {
        if (msg.getCommand().equals(Command.AUTHORIZATION_RES)){
            CommandPacket commandPacket = (CommandPacket)msg;
            Header header = commandPacket.getHeader();
            ByteBuf byteBuf = Unpooled.wrappedBuffer(commandPacket.getBytes());
            AuthenticationResponse authenticationResponse = new AuthenticationResponse(byteBuf,header);
            if (authenticationResponse.isAuthenticated()){
                UserInfo userInfo = authenticationResponse.getUserInfo();
                //用户信息
                DefaultClientUserChannelContext clientContext = (DefaultClientUserChannelContext) ClientContextHolder.clientContext();
                clientContext.setUserInfo(userInfo);
                //添加业务命令处理器并移除自身
                if (clientCmdHandlerHandler == null){
                    this.clientCmdHandlerHandler = new ClientCmdHandlerHandler();
                }
                channelHandlerContext.pipeline().addLast(clientCmdHandlerHandler).remove(this);
                //将业务命令处理器引用发布到控制层
                channelHandlerContext.pipeline().fireChannelActive();
            }else {
                log.warn("Login failed");
            }
            publishMessage(authenticationResponse);
        }
    }


    public void setClientCmdHandlerHandler(ChannelHandler channelHandler) {
        this.clientCmdHandlerHandler = (ClientCmdHandlerHandler) channelHandler;
    }

    public ClientCmdHandlerHandler getClientCmdHandlerHandler() {
        return clientCmdHandlerHandler;
    }

}
