package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import org.imtp.common.enums.Command;
import org.imtp.common.enums.MessageState;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.Packet;


/**
 * @Description 默认的消息响应
 * @Author ys
 * @Date 2024/4/8 15:05
 */
@Getter
public class MessageStateResponse extends SystemTextMessage {

    private MessageState state;

    public MessageStateResponse(MessageState state, Header header){
        //服务器收到消息回复一个已送达响应给到客户端
        super(0, header.getReceiver(), Command.MSG_RES);
        this.state = state;
    }

    public MessageStateResponse(ByteBuf byteBuf, Header header){
        super(byteBuf,header);
        byte res = byteBuf.readByte();
        this.state = MessageState.find(res);
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {
        byteBuf.writeByte((byte)state.ordinal());
    }

    @Override
    public int getBodyLength0() {
        return 1;
    }
}
