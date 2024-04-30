package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import org.imtp.common.enums.Command;
import org.imtp.common.enums.LoginState;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.packet.body.UserInfo;
import org.imtp.common.utils.JsonUtil;

import java.nio.charset.StandardCharsets;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/9 16:23
 */
@Getter
public class LoginResponse extends Packet {

    private LoginState loginState;

    private UserInfo userInfo;

    public LoginResponse(LoginState loginState,Long receiver) {
        this(loginState,receiver,null);
    }

    public LoginResponse(LoginState loginState,Long receiver,UserInfo userInfo) {
        super(0, receiver, Command.LOGIN_RES);
        this.loginState = loginState;
        this.userInfo = userInfo;
    }

    public LoginResponse(ByteBuf byteBuf, Header header) {
        super(header);
        byte b = byteBuf.readByte();
        this.loginState = LoginState.find(b);
        if(byteBuf.isReadable()){
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            this.userInfo = JsonUtil.parseObject(bytes,UserInfo.class);
        }
    }

    @Override
    public void encodeBodyAsByteBuf(ByteBuf byteBuf) {
        byteBuf.writeByte((byte)loginState.ordinal());
        if(userInfo != null){
            String jsonString = JsonUtil.toJSONString(userInfo);
            byte[] bytes = jsonString.getBytes(StandardCharsets.UTF_8);
            byteBuf.writeBytes(bytes);
        }
    }

    @Override
    public int getBodyLength() {
        if(this.userInfo != null){
            return 1 + JsonUtil.toJSONString(userInfo).getBytes(StandardCharsets.UTF_8).length;
        }
        return 1;
    }
}
