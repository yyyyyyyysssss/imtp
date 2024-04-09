package org.imtp.packet;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import org.imtp.enums.Command;
import org.imtp.enums.MessageState;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/8 15:05
 */
@Getter
public class DefaultMessageResponse extends Packet{

    private MessageState state;

    public DefaultMessageResponse(ByteBuf byteBuf,Header header){
        super(header);
        byte res = byteBuf.readByte();
        this.state = MessageState.find(res);
    }

    public DefaultMessageResponse(MessageState state){
        //服务器收到消息回复一个已送达响应给到客户端
        super(-1, -1, Command.TEXT_MSG_RES, 1);
        this.state = state;
    }

    @Override
    public void encodeBodyAsByteBuf(ByteBuf byteBuf) {
        byteBuf.writeByte((byte)state.ordinal());
    }
}
