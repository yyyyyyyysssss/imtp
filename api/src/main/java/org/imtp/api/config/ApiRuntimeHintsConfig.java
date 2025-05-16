package org.imtp.api.config;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.security.authentication.ProviderManager;

/**
 * @Description
 * @Author ys
 * @Date 2025/3/28 11:34
 */
@Configuration
@ImportRuntimeHints(ApiRuntimeHintsConfig.class)
public class ApiRuntimeHintsConfig implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        //反射
        hints.reflection().registerType(ProviderManager.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
        //资源
        hints.resources().registerPattern("config/application.yaml");
    }
}
