package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import org.imtp.common.enums.MessageTypeV2;
import org.imtp.common.packet.ImageMessage;
import org.imtp.common.packet.TextMessageV2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/22 14:43
 */
@Component("image")
@ChannelHandler.Sharable
public class ImageMessageHandlerV2 extends ForwardMessageHandler<ImageMessage> {

}
