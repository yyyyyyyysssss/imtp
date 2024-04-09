package org.imtp.packet;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import org.imtp.enums.Command;

import java.nio.charset.StandardCharsets;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/9 15:57
 */
@Getter
public class LoginRequest extends Packet{

    private String username;
    private String password;

    public LoginRequest(String username,String password) {
        super(0, 0, Command.LOGIN_REQ, username.length() + password.length() + 8);
        this.username = username;
        this.password = password;
    }

    public LoginRequest(ByteBuf byteBuf,Header header) {
        super(header);
        //解码username
        int uLen = byteBuf.readInt();
        byte[] bytes = new byte[uLen];
        byteBuf.readBytes(bytes);
        this.username = new String(bytes, StandardCharsets.UTF_8);
        //解码password
        int pLen = byteBuf.readInt();
        bytes = new byte[pLen];
        byteBuf.readBytes(bytes);
        this.password = new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void encodeBodyAsByteBuf(ByteBuf byteBuf) {
        int uLen = this.username.length();
        byteBuf.writeInt(uLen);
        byteBuf.writeBytes(this.username.getBytes(StandardCharsets.UTF_8));
        int pLen = this.password.length();
        byteBuf.writeInt(pLen);
        byteBuf.writeBytes(this.password.getBytes(StandardCharsets.UTF_8));
    }
}
