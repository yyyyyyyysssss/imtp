package org.imtp.server.mq;

import lombok.extern.slf4j.Slf4j;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.context.ChannelSession;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/9 10:43
 */
@Slf4j
public class ChatMessageDelegate implements MessageDelegate{

    @Override
    public void handleMessage(String message) {
        log.info("message:{}",message);
    }

    @Override
    public void handleMessage(ForwardMessage forwardMessage) {
        log.info("forwardMessage:{}",forwardMessage);
        for (String channelId : forwardMessage.getChannelIds()){
            ChannelSession channel = ChannelContextHolder.channelContext().getChannel(channelId);
            if(channel != null && channel.isActive()){
                channel.sendMessage(forwardMessage.getMessage());
            }
        }
    }
}
