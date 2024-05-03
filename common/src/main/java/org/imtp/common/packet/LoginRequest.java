package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;
import lombok.Getter;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.body.LoginInfo;
import org.imtp.common.utils.JsonUtil;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/9 15:57
 */
@Getter
public class LoginRequest extends SystemTextMessage {

    private LoginInfo loginInfo;

    public LoginRequest(LoginInfo loginInfo) {
        super(0, 0, Command.LOGIN_REQ);
        if (loginInfo == null){
            throw new NullPointerException("loginInfo is null");
        }
        this.text = JsonUtil.toJSONString(loginInfo);

    }

    public LoginRequest(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
        if(!StringUtil.isNullOrEmpty(this.text)){
            this.loginInfo = JsonUtil.parseObject(this.text, LoginInfo.class);
        }
    }


    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

    @Override
    public int getBodyLength0() {
        return 0;
    }
}
