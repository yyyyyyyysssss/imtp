package org.imtp.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.*;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.Packet;
import org.springframework.stereotype.Component;

/**
 * @Description 用于命令分发到具体处理器处理
 * @Author ys
 * @Date 2024/4/21 12:58
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class CommandHandler extends AbstractHandler<Packet> {

    @Resource
    private TextMessageHandler textMessageHandler;

    @Resource
    private ImageMessageHandler imageMessageHandler;

    @Resource
    private VideoMessageHandler videoMessageHandler;

    @Resource
    private VoiceMessageHandler voiceMessageHandler;

    @Resource
    private FileMessageHandler fileMessageHandler;

    @Resource
    private VoiceCallMessageHandler voiceCallMessageHandler;

    @Resource
    private VideoCallMessageHandler videoCallMessageHandler;

    @Resource
    private SignalingOfferHandler signalingOfferHandler;

    @Resource
    private SignalingAnswerHandler signalingAnswerHandler;

    @Resource
    private SignalingCandidateHandler signalingCandidateHandler;

    @Resource
    private SignalingCloseHandler signalingCloseHandler;

    @Resource
    private SignalingBusyHandler signalingBusyHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        if(packet instanceof CommandPacket commandPacket){
            Header header = commandPacket.getHeader();
            Command cmd = header.getCmd();
            ByteBuf byteBuf = Unpooled.wrappedBuffer(commandPacket.getBytes());
            try {
                switch (cmd) {
                    case TEXT_MESSAGE:
                        packet = new TextMessage(byteBuf,header).additionTimestamp();
                        if (channelHandlerContext.pipeline().get(TextMessageHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(textMessageHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    case IMAGE_MESSAGE:
                        packet = new ImageMessage(byteBuf,header).additionTimestamp();
                        if (channelHandlerContext.pipeline().get(ImageMessageHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(imageMessageHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    case VIDEO_MESSAGE:
                        packet = new VideoMessage(byteBuf,header).additionTimestamp();
                        if (channelHandlerContext.pipeline().get(VideoMessageHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(videoMessageHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    case VOICE_MESSAGE:
                        packet = new VoiceMessage(byteBuf,header).additionTimestamp();
                        if (channelHandlerContext.pipeline().get(VoiceMessageHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(voiceMessageHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    case FILE_MESSAGE:
                        packet = new FileMessage(byteBuf,header).additionTimestamp();
                        if (channelHandlerContext.pipeline().get(FileMessageHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(fileMessageHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    case VOICE_CALL_MESSAGE:
                        packet = new VoiceCallMessage(byteBuf,header).additionTimestamp();
                        if (channelHandlerContext.pipeline().get(VoiceCallMessageHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(voiceCallMessageHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    case VIDEO_CALL_MESSAGE:
                        packet = new VideoCallMessage(byteBuf,header).additionTimestamp();
                        if (channelHandlerContext.pipeline().get(VideoCallMessageHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(videoCallMessageHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    case SIGNALING_OFFER:
                        packet = new SignalingOfferMessage(byteBuf,header);
                        if (channelHandlerContext.pipeline().get(SignalingOfferHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(signalingOfferHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    case SIGNALING_ANSWER:
                        packet = new SignalingAnswerMessage(byteBuf,header);
                        if (channelHandlerContext.pipeline().get(SignalingAnswerHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(signalingAnswerHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    case SIGNALING_CANDIDATE:
                        packet = new SignalingCandidateMessage(byteBuf,header);
                        if (channelHandlerContext.pipeline().get(SignalingCandidateHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(signalingCandidateHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    case SIGNALING_BUSY:
                        packet = new SignalingBusyMessage(byteBuf,header);
                        if (channelHandlerContext.pipeline().get(SignalingBusyHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(signalingBusyHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    case SIGNALING_CLOSE:
                        packet = new SignalingCloseMessage(byteBuf,header);
                        if (channelHandlerContext.pipeline().get(SignalingCloseHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(signalingCloseHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    default:
                        throw new UnsupportedOperationException("不支持的操作");
                }
            }finally {
                byteBuf.release();
            }

        }else {
            channelHandlerContext.fireChannelRead(packet);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.warn("im server channelInactive:{}",ctx.channel().id().asLongText());
        Channel channel = ctx.channel();
        channelInactiveHandle(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel channel = ctx.channel();
        channelInactiveHandle(channel);
        log.error("exception message",cause);
        ctx.close();
    }
}
