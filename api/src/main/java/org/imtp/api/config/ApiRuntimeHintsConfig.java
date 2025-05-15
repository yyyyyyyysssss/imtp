package org.imtp.api.config;

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
        
        //资源
        hints.resources().registerPattern("config/application.yaml");
    }
}
