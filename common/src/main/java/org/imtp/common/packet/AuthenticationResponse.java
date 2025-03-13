package org.imtp.common.packet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.body.UserInfo;
import org.imtp.common.utils.JsonUtil;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/11 13:24
 */
public class AuthenticationResponse extends AbstractSystemMessage {

    private boolean authenticated;

    private UserInfo userInfo;

    public AuthenticationResponse(boolean authorized) {
        this(authorized,null);
    }

    public AuthenticationResponse(boolean authorized,UserInfo userInfo) {
        super(0, 0, Command.AUTHORIZATION_RES);
        this.authenticated = authorized;
        this.userInfo = userInfo;
        if(userInfo != null){
            this.text = JsonUtil.toJSONString(userInfo);
        }
    }

    public AuthenticationResponse(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
        if(!StringUtil.isNullOrEmpty(this.text)){
            this.userInfo = JsonUtil.parseObject(this.text, UserInfo.class);
        }
        byte b = byteBuf.readByte();
        this.authenticated = b == 1;
    }


    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {
        byteBuf.writeByte(authenticated ? 1 : 0);
    }

    @Override
    @JsonIgnore
    public int getBodyLength0() {
        return 1;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}
