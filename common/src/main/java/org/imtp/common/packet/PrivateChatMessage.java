package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.Command;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/22 13:59
 */
@Getter
@Setter
public class PrivateChatMessage extends TextMessage{
    public PrivateChatMessage(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    public PrivateChatMessage(String message, long sender, long receiver) {
        super(message, sender, receiver, Command.PRIVATE_CHAT_MSG);
    }
}
