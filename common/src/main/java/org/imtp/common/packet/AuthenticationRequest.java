package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/11 11:48
 */
public class AuthenticationRequest extends AbstractSystemMessage {

    private String token;

    public AuthenticationRequest(String token) {
        super(0, 0, Command.AUTHORIZATION_REQ);
        if (token == null || token.isEmpty()) {
            throw new NullPointerException("token is null");
        }
        this.text = token;

    }

    public AuthenticationRequest(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
        if (!StringUtil.isNullOrEmpty(this.text)) {
            this.token = this.text;
        }
    }


    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

    @Override
    public int getBodyLength0() {
        return 0;
    }

    public String getToken() {
        return token;
    }
}
