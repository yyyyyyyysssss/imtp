package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import org.imtp.common.packet.VoiceMessageV2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/22 14:43
 */
@Component("voice")
@ChannelHandler.Sharable
public class VoiceMessageHandlerV2 extends ForwardMessageHandler<VoiceMessageV2> {

}
