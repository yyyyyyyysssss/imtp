package org.imtp.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.imtp.common.enums.LoginState;
import org.imtp.common.packet.LoginRequest;
import org.imtp.common.packet.LoginResponse;
import org.imtp.common.packet.Packet;
import org.imtp.server.utils.CacheUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/21 15:06
 */
public class LoginHandler extends SimpleChannelInboundHandler<LoginRequest> {

    //测试登录
    private final List<String> testUsernames = new ArrayList<>(){{add("1085385084");add("18855193274");}};
    private final String testPassword = "136156";

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginRequest loginRequest) throws Exception {
        if(testUsernames.contains(loginRequest.getUsername()) && loginRequest.getPassword().equals(testPassword)){
            channelHandlerContext.channel().writeAndFlush(new LoginResponse(LoginState.SUCCESS));
            //记录channel
            CacheUtil.putChannel(Long.parseLong(loginRequest.getUsername()),channelHandlerContext.channel());
            //登录成功则移除当前handler
            channelHandlerContext.pipeline().remove(this);
        }else {
            channelHandlerContext.channel().writeAndFlush(new LoginResponse(LoginState.FAIL));
        }
    }
}
