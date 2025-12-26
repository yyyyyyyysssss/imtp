package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import org.imtp.common.packet.ImageMessageV2;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/22 14:43
 */
@Component("image")
@ChannelHandler.Sharable
public class ImageMessageHandlerV2 extends MessageDispatchHandler<ImageMessageV2> {

}
