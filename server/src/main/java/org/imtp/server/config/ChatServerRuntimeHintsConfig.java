package org.imtp.server.config;

import org.imtp.server.handler.*;
import org.imtp.server.restclient.ChatApi;
import org.imtp.server.service.impl.UserStatusServiceImpl;
import org.springframework.aot.hint.MemberCategory;
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
        //反射
        hints.reflection().registerType(ChatApi.class, MemberCategory.values());
        hints.reflection().registerType(UserStatusServiceImpl.class, MemberCategory.values());
        hints.reflection().registerType(AuthenticationHandler.class, MemberCategory.values());
        hints.reflection().registerType(CommandHandler.class, MemberCategory.values());
        hints.reflection().registerType(TextMessageHandler.class, MemberCategory.values());
        hints.reflection().registerType(VideoMessageHandler.class, MemberCategory.values());
        hints.reflection().registerType(VoiceMessageHandler.class, MemberCategory.values());
        hints.reflection().registerType(FileMessageHandler.class, MemberCategory.values());
        hints.reflection().registerType(VideoCallMessageHandler.class, MemberCategory.values());
        hints.reflection().registerType(VoiceCallMessageHandler.class, MemberCategory.values());
        hints.reflection().registerType(HeartbeatPingHandler.class, MemberCategory.values());
        hints.reflection().registerType(HeartbeatPongHandler.class, MemberCategory.values());
        hints.reflection().registerType(ImageMessageHandler.class, MemberCategory.values());
        hints.reflection().registerType(SignalingAnswerHandler.class, MemberCategory.values());
        hints.reflection().registerType(SignalingBusyHandler.class, MemberCategory.values());
        hints.reflection().registerType(SignalingCandidateHandler.class, MemberCategory.values());
        hints.reflection().registerType(SignalingCloseHandler.class, MemberCategory.values());
        hints.reflection().registerType(SignalingOfferHandler.class, MemberCategory.values());
        hints.reflection().registerType(SignalingPreOfferHandler.class, MemberCategory.values());
        hints.reflection().registerType(WebSocketAdapterHandler.class, MemberCategory.values());
    }
}
