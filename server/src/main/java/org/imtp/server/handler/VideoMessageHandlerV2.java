package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import org.imtp.common.packet.VideoMessageV2;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/22 14:43
 */
@Component("video")
@ChannelHandler.Sharable
public class VideoMessageHandlerV2 extends MessageDispatchHandler<VideoMessageV2> {

}
