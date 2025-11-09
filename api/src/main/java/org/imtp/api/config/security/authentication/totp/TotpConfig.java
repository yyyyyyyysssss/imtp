package org.imtp.api.config.security.authentication.totp;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TotpConfig {

    @Bean
    public GoogleAuthenticator googleAuthenticator(GoogleAuthenticatorConfig googleAuthenticatorConfig) {

        return new GoogleAuthenticator(googleAuthenticatorConfig);
    }

    @Bean
    public GoogleAuthenticatorConfig googleAuthenticatorConfig() {
        return new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setTimeStepSizeInMillis(60000) // TOTP 一次有效的时间周期
                .setWindowSize(1) // 容错1个时间片，±30秒
                .build();
    }

}
