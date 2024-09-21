package org.imtp.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.packet.AuthenticationRequest;
import org.imtp.common.packet.AuthenticationResponse;
import org.imtp.common.packet.CommandPacket;
import org.imtp.common.packet.LoginRequest;
import org.imtp.common.packet.body.UserInfo;
import org.imtp.common.response.Result;
import org.imtp.common.utils.JsonUtil;
import org.imtp.server.constant.ProjectConstant;
import org.imtp.server.context.*;
import org.imtp.server.feign.WebApi;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/11 11:47
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class AuthenticationHandler extends AbstractHandler<Object>{

    @Resource
    private WebApi webApi;

    @Resource
    private CommandHandler commandHandler;

    @Resource
    private WebSocketAdapterHandler webSocketAdapterHandler;

    private static final AuthenticationResponse DENY = new AuthenticationResponse(false);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof TextWebSocketFrame textWebSocketFrame){
            String token = textWebSocketFrame.text();
            Result<UserInfo> result = webApi.tokenValid(token);
            if(result.isSucceed()){
                UserInfo userInfo = result.getData();
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.toJSONString(new AuthenticationResponse(true,userInfo))));
                ctx.pipeline().addLast(webSocketAdapterHandler).remove(this);
                authenticationSuccess(new WebSocketChannelSession(ctx.channel(),userInfo.getId().toString()));
            }else {
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.toJSONString(DENY)));
            }
        }else if(msg instanceof CommandPacket commandPacket){
            ByteBuf byteBuf = Unpooled.wrappedBuffer(commandPacket.getBytes());
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(byteBuf,commandPacket.getHeader());
            String token = authenticationRequest.getToken();
            Result<UserInfo> result = webApi.tokenValid(token);
            if(result.isSucceed()){
                UserInfo userInfo = result.getData();
                ctx.channel().writeAndFlush(new AuthenticationResponse(true,userInfo));
                ctx.pipeline().addLast(commandHandler).remove(this);
                authenticationSuccess(new IMChannelSession(ctx.channel(),userInfo.getId().toString()));
            }else {
                ctx.channel().writeAndFlush(DENY);
            }
        }else {
            throw new UnsupportedOperationException("Unknown command");
        }
    }


    private void authenticationSuccess(ChannelSession channelSession){
        Channel channel = channelSession.channel();
        String userId = channelSession.userId();
        //保存channel
        ChannelContextHolder.channelContext().addChannel(channelSession.id(),channelSession);
        //建立channel与用户之间的关系
        channel.attr(AttributeKey.valueOf(ProjectConstant.CHANNEL_ATTR_LOGIN_USER)).set(userId);
        log.info("用户:{} 已上线",userId);
        chatService.userOnline(userId, channelSession.id());
    }

}
