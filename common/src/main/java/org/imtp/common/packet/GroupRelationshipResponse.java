package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.body.UserGroupInfo;
import org.imtp.common.utils.JsonUtil;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/29 15:58
 */
public class GroupRelationshipResponse extends SystemTextMessage{

    private List<UserGroupInfo> userGroupInfos;

    public GroupRelationshipResponse(Long receiver) {
        this(receiver,null);
    }

    public GroupRelationshipResponse(Long receiver, List<UserGroupInfo> userGroupInfos) {
        super(0, receiver, Command.GROUP_RELATIONSHIP_RES);
        if(userGroupInfos != null && !userGroupInfos.isEmpty()){
            this.text = JsonUtil.toJSONString(userGroupInfos);
        }
    }

    public GroupRelationshipResponse(ByteBuf byteBuf, Header header) {
        super(byteBuf,header);
        if(!StringUtil.isNullOrEmpty(this.text)){
            this.userGroupInfos = JsonUtil.parseArray(this.text,UserGroupInfo.class);
        }
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

    @Override
    public int getBodyLength0() {
        return 0;
    }

    public List<UserGroupInfo> getUserGroupInfos() {
        return userGroupInfos;
    }

}
