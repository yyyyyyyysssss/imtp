package org.imtp.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.enums.LoginState;
import org.imtp.common.packet.LoginRequest;
import org.imtp.common.packet.LoginResponse;
import org.imtp.server.constant.ProjectConstant;
import org.imtp.server.context.ChannelContext;
import org.imtp.server.context.ChannelContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/21 15:06
 */
@Slf4j
public class LoginHandler extends SimpleChannelInboundHandler<LoginRequest> {

    //测试登录
    private final List<String> testUsernames = new ArrayList<>(){{add("147");add("258");add("369");}};
    private final String testPassword = "123456";

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginRequest loginRequest) {
        if(testUsernames.contains(loginRequest.getUsername()) && loginRequest.getPassword().equals(testPassword)){
            channelHandlerContext.channel().writeAndFlush(new LoginResponse(LoginState.SUCCESS, Long.valueOf(loginRequest.getUsername())));
            //登录成功则移除当前handler
            channelHandlerContext.pipeline().remove(this);

            //存储channel
            ChannelContext channelContext = ChannelContextHolder.createChannelContext();
            channelContext.addChannel(loginRequest.getUsername(),channelHandlerContext.channel());
            //建立channel与用户之间的关系
            AttributeKey<Long> attributeKey = AttributeKey.valueOf(ProjectConstant.CHANNEL_ATTR_LOGIN_USER);
            channelHandlerContext.channel().attr(attributeKey).set(Long.parseLong(loginRequest.getUsername()));
            log.info("用户:{} 已上线",loginRequest.getUsername());
        }else {
            channelHandlerContext.channel().writeAndFlush(new LoginResponse(LoginState.FAIL,Long.valueOf(loginRequest.getUsername())));
            log.info("用户:{} 用户名或密码错误",loginRequest.getUsername());
        }
    }
}
