package org.imtp.server.mq;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.imtp.server.context.ChannelContextHolder;

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
        for (String receiver : forwardMessage.getReceivers()){
            Channel channel = ChannelContextHolder.createChannelContext().getChannel(receiver);
            if(channel != null && channel.isActive()){
                channel.writeAndFlush(forwardMessage.getMessage());
            }
        }
    }
}
