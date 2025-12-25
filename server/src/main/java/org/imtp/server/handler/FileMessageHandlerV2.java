package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import org.imtp.common.packet.FileMessageV2;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/22 14:43
 */
@Component("file")
@ChannelHandler.Sharable
public class FileMessageHandlerV2 extends MessageDispatchHandler<FileMessageV2> {

}
