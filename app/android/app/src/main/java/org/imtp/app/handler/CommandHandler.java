package org.imtp.app.handler;

import android.util.Log;

import org.imtp.app.NettyClient;
import org.imtp.app.model.MessageModel;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.CommandPacket;
import org.imtp.common.packet.FileMessage;
import org.imtp.common.packet.HeartbeatPingMessage;
import org.imtp.common.packet.ImageMessage;
import org.imtp.common.packet.MessageStateResponse;
import org.imtp.common.packet.TextMessage;
import org.imtp.common.packet.VideoMessage;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class CommandHandler extends AbstractModelHandler<Packet> {

    private static final String TAG = "CommandHandler";

    public CommandHandler(NettyClient nettyClient, MessageModel model){
        super(nettyClient,model);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        CommandPacket commandPacket = (CommandPacket)packet;
        Header header = commandPacket.getHeader();
        Command cmd = header.getCmd();
        ByteBuf byteBuf = Unpooled.wrappedBuffer(commandPacket.getBytes());
        switch (cmd){
            case TEXT_MESSAGE:
                this.model.publishMessage(new TextMessage(byteBuf, header));
                break;
            case IMAGE_MESSAGE:
                this.model.publishMessage(new ImageMessage(byteBuf, header));
                break;
            case VIDEO_MESSAGE:
                this.model.publishMessage(new VideoMessage(byteBuf, header));
                break;
            case FILE_MESSAGE:
                this.model.publishMessage(new FileMessage(byteBuf, header));
                break;
            case HEARTBEAT_PING:
                this.model.publishMessage(new HeartbeatPingMessage(byteBuf, header));
                break;
            case MSG_RES:
                this.model.publishMessage(new MessageStateResponse(byteBuf, header));
                break;
            default:
                Log.e(TAG,"Unsupported Operation");
        }
    }
}
