package org.imtp.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.enums.LoginState;
import org.imtp.common.packet.*;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.packet.body.UserInfo;
import org.imtp.server.constant.ProjectConstant;
import org.imtp.server.context.ChannelContext;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.entity.User;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/21 15:06
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class LoginHandler extends AbstractHandler<Packet> {

    @Resource
    private CommandHandler commandHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        CommandPacket commandPacket = (CommandPacket) packet;
        ByteBuf byteBuf = Unpooled.wrappedBuffer(commandPacket.getBytes());
        LoginRequest loginRequest = new LoginRequest(byteBuf,commandPacket.getHeader());
        User user = chatService.findByUsername(loginRequest.getUsername());
        if(user != null && user.getPassword().equals(loginRequest.getPassword())){
            //登录成功将用户信息携带回去
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setPassword(user.getPassword());
            userInfo.setAvatar(user.getAvatar());
            userInfo.setNickname(user.getNickname());
            channelHandlerContext.channel().writeAndFlush(new LoginResponse(LoginState.SUCCESS, user.getId(),userInfo));

            //存储channel
            ChannelContext channelContext = ChannelContextHolder.createChannelContext();
            channelContext.addChannel(user.getId().toString(),channelHandlerContext.channel());
            //建立channel与用户之间的关系
            AttributeKey<Long> attributeKey = AttributeKey.valueOf(ProjectConstant.CHANNEL_ATTR_LOGIN_USER);
            channelHandlerContext.channel().attr(attributeKey).set(Long.parseLong(loginRequest.getUsername()));
            log.info("用户:{} 已上线",loginRequest.getUsername());

            //登录成功则添加业务指令处理器并移除当前handler
            channelHandlerContext.pipeline().addLast(commandHandler).remove(this);
        }else {
            channelHandlerContext.channel().writeAndFlush(new LoginResponse(LoginState.FAIL,Long.valueOf(loginRequest.getUsername())));
            log.info("用户:{} 用户名或密码错误",loginRequest.getUsername());
        }
    }
}
