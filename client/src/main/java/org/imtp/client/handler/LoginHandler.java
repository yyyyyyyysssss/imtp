package org.imtp.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.common.enums.LoginState;
import org.imtp.common.packet.*;
import org.imtp.common.packet.body.UserInfo;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/29 16:43
 */
@Slf4j
public class LoginHandler extends SimpleChannelInboundHandler<Packet> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        CommandPacket commandPacket = (CommandPacket)packet;
        Header header = commandPacket.getHeader();
        ByteBuf byteBuf = Unpooled.wrappedBuffer(commandPacket.getBytes());
        switch (packet.getCommand()){
            case LOGIN_RES :
                LoginResponse loginResponse = new LoginResponse(byteBuf,header);
                if(loginResponse.getLoginState().equals(LoginState.SUCCESS)){
                    log.info("登录成功");
                    UserInfo userInfo = loginResponse.getUserInfo();

                    //TODO 拉取好友关系 拉取离线消息
                    channelHandlerContext.channel().writeAndFlush(new PullFriendRequest(userInfo.getId()));

                    //初始化上下文对象
                    ClientContextHolder.createClientContext(channelHandlerContext.channel(), userInfo);
                    //添加业务命令处理器并移除自身
                    channelHandlerContext.pipeline().addLast(new ClientCmdHandlerHandler()).remove(this);
                    channelHandlerContext.pipeline().fireChannelActive();
                }else {
                    log.info("登录失败");
                    channelHandlerContext.channel().close();
                }
                break;
            default:
                throw new UnsupportedOperationException("当前不支持的操作");
        }
    }


}
