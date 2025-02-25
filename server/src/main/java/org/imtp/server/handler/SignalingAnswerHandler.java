package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import org.imtp.common.packet.SignalingAnswerMessage;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2025/2/25 11:03
 */
@Component
@ChannelHandler.Sharable
public class SignalingAnswerHandler extends AbstractSignalingHandler<SignalingAnswerMessage> {
}
