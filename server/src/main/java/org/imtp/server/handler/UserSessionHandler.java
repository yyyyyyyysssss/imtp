package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.imtp.common.packet.FriendshipResponse;
import org.imtp.common.packet.UserSessionRequest;
import org.imtp.common.packet.UserSessionResponse;
import org.imtp.common.packet.body.UserFriendInfo;
import org.imtp.common.packet.body.UserSessionInfo;
import org.imtp.server.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        List<UserSessionInfo> userSessionInfos = chatService.findUserSessionByUserId(userSessionRequest.getSender());
        if(userSessionInfos.isEmpty()){
            channelHandlerContext.channel().writeAndFlush(new UserSessionRequest(userSessionRequest.getSender()));
            return;
        }
        channelHandlerContext.channel().writeAndFlush(new UserSessionResponse(userSessionRequest.getSender(),userSessionInfos));
    }
}
