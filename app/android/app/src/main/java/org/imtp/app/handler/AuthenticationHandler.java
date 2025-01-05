package org.imtp.app.handler;

import android.util.Log;

import org.imtp.app.context.ClientContextHolder;
import org.imtp.app.model.MessageModel;
import org.imtp.app.model.Model;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.AuthenticationResponse;
import org.imtp.common.packet.CommandPacket;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/21 18:55
 */
@ChannelHandler.Sharable
public class AuthenticationHandler extends AbstractModelHandler<Packet> {

    private static final String TAG = "AuthenticationHandler";

    public AuthenticationHandler(MessageModel model) {
        super(model);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet msg) {
        if (msg.getCommand().equals(Command.AUTHORIZATION_RES)){
            CommandPacket commandPacket = (CommandPacket)msg;
            Header header = commandPacket.getHeader();
            ByteBuf byteBuf = Unpooled.wrappedBuffer(commandPacket.getBytes());
            AuthenticationResponse authenticationResponse = new AuthenticationResponse(byteBuf,header);
            if (authenticationResponse.isAuthenticated()){
                Log.i(TAG,"Authentication Succeed");
                channelHandlerContext.pipeline().addLast(new CommandHandler(this.model)).remove(this);
            }else {
                Log.e(TAG,"Authentication Failed");
            }
        }
    }

}
