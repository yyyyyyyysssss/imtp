package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;
import lombok.Getter;
import org.imtp.common.enums.Command;
import org.imtp.common.enums.LoginState;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.body.UserInfo;
import org.imtp.common.utils.JsonUtil;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/9 16:23
 */
@Getter
public class LoginResponse extends SystemTextMessage {

    private LoginState loginState;

    private UserInfo userInfo;

    public LoginResponse(LoginState loginState,Long receiver) {
        this(loginState,receiver,null);
    }

    public LoginResponse(LoginState loginState,Long receiver,UserInfo userInfo) {
        super(0, receiver, Command.LOGIN_RES);
        this.loginState = loginState;
        this.userInfo = userInfo;
        if(userInfo != null){
            this.text = JsonUtil.toJSONString(userInfo);
        }
    }

    public LoginResponse(ByteBuf byteBuf, Header header) {
        super(byteBuf,header);
        if(!StringUtil.isNullOrEmpty(this.text)){
            this.userInfo = JsonUtil.parseObject(this.text, UserInfo.class);
        }
        byte b = byteBuf.readByte();
        this.loginState = LoginState.find(b);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {
        byteBuf.writeByte((byte)loginState.ordinal());
    }

    @Override
    public int getBodyLength0() {
        return 1;
    }
}
