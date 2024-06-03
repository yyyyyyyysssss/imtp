package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.imtp.common.packet.GroupRelationshipRequest;
import org.imtp.common.packet.GroupRelationshipResponse;
import org.imtp.common.packet.body.UserGroupInfo;
import org.imtp.server.entity.Group;
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
public class UserGroupRelationshipHandler extends AbstractHandler<GroupRelationshipRequest>{


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupRelationshipRequest pullFriendRequest) {
        List<UserGroupInfo> groupInfos = chatService.findGroupByUserId(pullFriendRequest.getSender());
        if(groupInfos.isEmpty()){
            channelHandlerContext.channel().writeAndFlush(new GroupRelationshipResponse(pullFriendRequest.getSender()));
            return;
        }
        channelHandlerContext.channel().writeAndFlush(new GroupRelationshipResponse(pullFriendRequest.getSender(),groupInfos));
    }
}
