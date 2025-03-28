package org.imtp.server.config;

import org.imtp.server.handler.*;
import org.imtp.server.restclient.ChatApi;
import org.imtp.server.service.impl.UserStatusServiceImpl;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * @Description
 * @Author ys
 * @Date 2025/3/28 11:34
 */
@Configuration
@ImportRuntimeHints(ChatServerRuntimeHintsConfig.class)
public class ChatServerRuntimeHintsConfig implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(ChatApi.class);
        hints.reflection().registerType(UserStatusServiceImpl.class);
        hints.reflection().registerType(AuthenticationHandler.class);
        hints.reflection().registerType(CommandHandler.class);
        hints.reflection().registerType(TextMessageHandler.class);
        hints.reflection().registerType(VideoMessageHandler.class);
        hints.reflection().registerType(VoiceMessageHandler.class);
        hints.reflection().registerType(FileMessageHandler.class);
        hints.reflection().registerType(VideoCallMessageHandler.class);
        hints.reflection().registerType(VoiceCallMessageHandler.class);
        hints.reflection().registerType(HeartbeatPingHandler.class);
        hints.reflection().registerType(HeartbeatPongHandler.class);
        hints.reflection().registerType(ImageMessageHandler.class);
        hints.reflection().registerType(SignalingAnswerHandler.class);
        hints.reflection().registerType(SignalingBusyHandler.class);
        hints.reflection().registerType(SignalingCandidateHandler.class);
        hints.reflection().registerType(SignalingCloseHandler.class);
        hints.reflection().registerType(SignalingOfferHandler.class);
        hints.reflection().registerType(SignalingPreOfferHandler.class);
        hints.reflection().registerType(WebSocketAdapterHandler.class);
    }
}
