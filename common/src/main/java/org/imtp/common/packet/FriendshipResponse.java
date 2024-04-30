package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.body.UserFriendInfo;
import org.imtp.common.utils.JsonUtil;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/29 15:58
 */
public class FriendshipResponse extends SystemTextMessage{

    private List<UserFriendInfo> userFriendInfos;

    public FriendshipResponse(Long receiver) {
        this(receiver,null);
    }

    public FriendshipResponse(Long receiver, List<UserFriendInfo> userFriendInfos) {
        super(0, receiver, Command.FRIENDSHIP_RES);
        if(userFriendInfos != null && !userFriendInfos.isEmpty()){
            this.text = JsonUtil.toJSONString(userFriendInfos);
        }
    }

    public FriendshipResponse(ByteBuf byteBuf, Header header) {
        super(byteBuf,header);
        if(!StringUtil.isNullOrEmpty(this.text)){
            this.userFriendInfos = JsonUtil.parseArray(this.text,UserFriendInfo.class);
        }
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

    public List<UserFriendInfo> getUserFriendInfos() {
        return userFriendInfos;
    }

}
