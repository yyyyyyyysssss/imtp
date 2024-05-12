package org.imtp.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.context.ClientContext;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.context.DefaultClientUserChannelContext;
import org.imtp.client.model.MessageModel;
import org.imtp.common.enums.LoginState;
import org.imtp.common.packet.*;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.packet.body.UserInfo;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/29 16:43
 */
@Slf4j
@ChannelHandler.Sharable
public class LoginHandler extends AbstractMessageModelHandler<Packet> {

    private ClientCmdHandlerHandler clientCmdHandlerHandler;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //初始化上下文对象
        ClientContextHolder.createClientContext(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        CommandPacket commandPacket = (CommandPacket)packet;
        Header header = commandPacket.getHeader();
        ByteBuf byteBuf = Unpooled.wrappedBuffer(commandPacket.getBytes());
        switch (packet.getCommand()){
            case LOGIN_RES :
                LoginResponse loginResponse = new LoginResponse(byteBuf,header);
                if(loginResponse.getLoginState().equals(LoginState.SUCCESS)){
                    UserInfo userInfo = loginResponse.getUserInfo();
                    //用户信息
                    DefaultClientUserChannelContext clientContext = (DefaultClientUserChannelContext)ClientContextHolder.clientContext();
                    clientContext.setUserInfo(userInfo);
                    //添加业务命令处理器并移除自身
                    this.clientCmdHandlerHandler = new ClientCmdHandlerHandler();
                    channelHandlerContext.pipeline().addLast(clientCmdHandlerHandler).remove(this);
                    //将业务命令处理器引用发布到控制层
                    channelHandlerContext.pipeline().fireChannelActive();
                }else {
                    log.warn("Login failed");
                }
                publishMessage(loginResponse);
                break;
            default:
                throw new UnsupportedOperationException("当前不支持的操作");
        }
    }


    @Override
    public MessageModel getNextModel() {
        return this.clientCmdHandlerHandler;
    }
}
