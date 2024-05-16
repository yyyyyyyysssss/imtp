package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.body.UserSessionInfo;
import org.imtp.common.utils.JsonUtil;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/16 17:11
 */
public class UserSessionResponse extends SystemTextMessage{

    private List<UserSessionInfo> userSessionInfos;

    public UserSessionResponse(Long receiver) {
        this(receiver,null);
    }

    public UserSessionResponse(Long receiver, List<UserSessionInfo> userSessionInfos) {
        super(0, receiver, Command.USER_SESSION_RES);
        if(userSessionInfos != null && !userSessionInfos.isEmpty()){
            this.text = JsonUtil.toJSONString(userSessionInfos);
        }
    }

    public UserSessionResponse(ByteBuf byteBuf, Header header) {
        super(byteBuf,header);
        if(!StringUtil.isNullOrEmpty(this.text)){
            this.userSessionInfos = JsonUtil.parseArray(this.text,UserSessionInfo.class);
        }
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

    @Override
    public int getBodyLength0() {
        return 0;
    }

    public List<UserSessionInfo> getUserSessionInfos() {
        return userSessionInfos;
    }

}
