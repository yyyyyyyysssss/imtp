package org.imtp.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.imtp.enums.LoginState;
import org.imtp.packet.Header;
import org.imtp.packet.LoginRequest;
import org.imtp.packet.LoginResponse;
import org.imtp.packet.Packet;
import org.imtp.utils.CacheUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/9 15:43
 */
public class InitializeHandler extends SimpleChannelInboundHandler<Packet> {
    //测试登录
    private final List<String> testUsernames = new ArrayList<>(){{add("1085385084");add("18855193274");}};
    private final String testPassword = "136156";

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        Header header = packet.getHeader();
        switch (header.getCmd()){
            case LOGIN_REQ :
                LoginRequest loginRequest = (LoginRequest)packet;
                if(testUsernames.contains(loginRequest.getUsername()) && loginRequest.getPassword().equals(testPassword)){
                    channelHandlerContext.channel().writeAndFlush(new LoginResponse(LoginState.SUCCESS));
                    //记录channel
                    CacheUtil.putChannel(Long.parseLong(loginRequest.getUsername()),channelHandlerContext.channel());
                    //登录成功则
                    channelHandlerContext.pipeline().addLast(new SendMessageHandler()).remove(this);
                }else {
                    channelHandlerContext.channel().writeAndFlush(new LoginResponse(LoginState.FAIL));
                }
                break;
            default:
                channelHandlerContext.channel().writeAndFlush(new LoginResponse(LoginState.NOT_LOGIN));
                channelHandlerContext.channel().close();
                break;
        }
    }
}
