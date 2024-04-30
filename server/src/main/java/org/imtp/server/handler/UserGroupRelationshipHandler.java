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
        List<Group> groups = chatService.findGroupByUserId(pullFriendRequest.getSender());
        if(groups.isEmpty()){
            channelHandlerContext.channel().writeAndFlush(new GroupRelationshipResponse(pullFriendRequest.getSender()));
            return;
        }
        List<UserGroupInfo> userGroupInfos = new ArrayList<>();
        for (Group group : groups){
            UserGroupInfo groupInfo = UserGroupInfo.builder().id(group.getId()).groupName(group.getName()).build();
            userGroupInfos.add(groupInfo);
        }
        channelHandlerContext.channel().writeAndFlush(new GroupRelationshipResponse(pullFriendRequest.getSender(),userGroupInfos));
    }
}
