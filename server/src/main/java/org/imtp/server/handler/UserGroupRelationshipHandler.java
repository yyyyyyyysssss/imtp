package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.imtp.common.packet.GroupRelationshipRequest;
import org.imtp.common.packet.GroupRelationshipResponse;
import org.imtp.common.packet.body.UserGroupInfo;
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
public class UserGroupRelationshipHandler extends AbstractHandler<GroupRelationshipRequest>{


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupRelationshipRequest pullFriendRequest) {
        Result<List<UserGroupInfo>> result = webApi.userGroup(pullFriendRequest.getSender().toString());
        List<UserGroupInfo> userGroupInfos;
        if (result.isSucceed() && !(userGroupInfos = result.getData()).isEmpty()){
            channelHandlerContext.channel().writeAndFlush(new GroupRelationshipResponse(pullFriendRequest.getSender(),userGroupInfos));
        }else {
            channelHandlerContext.channel().writeAndFlush(new GroupRelationshipResponse(pullFriendRequest.getSender()));
        }
    }
}
