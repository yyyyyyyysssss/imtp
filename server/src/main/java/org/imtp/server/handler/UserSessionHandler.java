package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.imtp.common.packet.UserSessionRequest;
import org.imtp.common.packet.UserSessionResponse;
import org.imtp.common.packet.body.UserSessionInfo;
import org.imtp.common.response.Result;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @Description 用于拉取用户好友关系的处理器
 * @Author ys
 * @Date 2024/4/30 11:51
 */
@Component
@ChannelHandler.Sharable
public class UserSessionHandler extends AbstractHandler<UserSessionRequest>{

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, UserSessionRequest userSessionRequest) {
        Result<List<UserSessionInfo>> result = webApi.userSession(userSessionRequest.getSender().toString());
        List<UserSessionInfo> userSessionInfos;
        if (result.isSucceed() && !(userSessionInfos = result.getData()).isEmpty()){
            channelHandlerContext.channel().writeAndFlush(new UserSessionResponse(userSessionRequest.getSender(),userSessionInfos));
        }else {
            channelHandlerContext.channel().writeAndFlush(new UserSessionResponse(userSessionRequest.getSender()));
        }
    }
}
