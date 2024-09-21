package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.imtp.common.packet.OfflineMessageRequest;
import org.imtp.common.packet.OfflineMessageResponse;
import org.imtp.common.packet.body.OfflineMessageInfo;
import org.imtp.common.response.Result;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/30 13:57
 */
@Component
@ChannelHandler.Sharable
public class OfflineMessageHandler extends AbstractHandler<OfflineMessageRequest>{

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, OfflineMessageRequest offlineMessageRequest) throws Exception {
        Result<List<OfflineMessageInfo>> result = webApi.offlineMessage(offlineMessageRequest.getSender().toString());
        List<OfflineMessageInfo> offlineMessageInfos;
        if (result.isSucceed() && !(offlineMessageInfos = result.getData()).isEmpty()){
            channelHandlerContext.channel().writeAndFlush(new OfflineMessageResponse(offlineMessageRequest.getSender(),offlineMessageInfos));
        }else {
            channelHandlerContext.channel().writeAndFlush(new OfflineMessageResponse(offlineMessageRequest.getSender()));
        }
    }


}
