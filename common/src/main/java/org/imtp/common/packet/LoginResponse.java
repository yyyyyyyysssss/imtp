package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import org.imtp.common.enums.Command;
import org.imtp.common.enums.LoginState;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/9 16:23
 */
@Getter
public class LoginResponse extends Packet{

    private LoginState loginState;

    public LoginResponse(LoginState loginState) {
        super(0, 0, Command.LOGIN_RES, 1);
        this.loginState = loginState;
    }

    public LoginResponse(ByteBuf byteBuf,Header header) {
        super(header);
        byte b = byteBuf.readByte();
        this.loginState = LoginState.find(b);
    }

    @Override
    public void encodeBodyAsByteBuf(ByteBuf byteBuf) {
        byteBuf.writeByte((byte)loginState.ordinal());
    }
}
