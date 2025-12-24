package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import org.imtp.common.packet.TextMessageV2;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/22 14:43
 */
@Component("text")
@ChannelHandler.Sharable
public class TextMessageHandlerV2 extends ForwardMessageHandler<TextMessageV2> {

}
