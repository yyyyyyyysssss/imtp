package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.Command;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/22 13:34
 */
@Getter
@Setter
public class GroupChatMessage extends TextMessage{


    public GroupChatMessage(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
    }

    public GroupChatMessage(String message, long sender, long receiver) {
        super(message, sender, receiver, Command.GROUP_CHAT_MSG);
    }
}
