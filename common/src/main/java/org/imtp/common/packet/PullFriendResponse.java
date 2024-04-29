package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.body.UserInfo;
import org.imtp.common.utils.JsonUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/29 15:58
 */
public class PullFriendResponse extends SystemTextMessage{

    private List<UserInfo> userInfos;

    public PullFriendResponse(Long receiver,List<UserInfo> userInfos) {
        super(0, receiver, Command.PULL_FRIEND_RES);
        this.text = JsonUtil.toJSONString(userInfos);
    }

    public PullFriendResponse(ByteBuf byteBuf,Header header) {
        super(byteBuf,header);
        this.userInfos = JsonUtil.parseArray(this.text,UserInfo.class);
    }

    @Override
    void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }
}
