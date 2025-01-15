package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import org.imtp.common.packet.VoiceMessage;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/22 14:43
 */
@Component
@ChannelHandler.Sharable
public class VoiceMessageHandler extends AbstractTextHandler<VoiceMessage> {

}
