package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.imtp.common.packet.FriendshipRequest;
import org.imtp.common.packet.FriendshipResponse;
import org.imtp.common.packet.body.UserFriendInfo;
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
public class UserFriendshipHandler extends AbstractHandler<FriendshipRequest>{


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FriendshipRequest friendshipRequest) {
        List<UserFriendInfo> userFriendInfos = chatService.findFriendByUserId(friendshipRequest.getSender());
        if(userFriendInfos.isEmpty()){
            channelHandlerContext.channel().writeAndFlush(new FriendshipResponse(friendshipRequest.getSender()));
            return;
        }
        channelHandlerContext.channel().writeAndFlush(new FriendshipResponse(friendshipRequest.getSender(),userFriendInfos));
    }
}
