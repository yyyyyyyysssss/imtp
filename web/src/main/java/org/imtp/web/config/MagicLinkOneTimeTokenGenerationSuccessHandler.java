package org.imtp.web.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler;

/**
 * @Description
 * @Author ys
 * @Date 2025/2/21 12:58
 */
@Slf4j
public class MagicLinkOneTimeTokenGenerationSuccessHandler implements OneTimeTokenGenerationSuccessHandler {

    private String loginPage;

    public MagicLinkOneTimeTokenGenerationSuccessHandler(String loginPage) {
        this.loginPage = loginPage;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, OneTimeToken oneTimeToken) {
        String magicLink = loginPage + "?ottToken=" + oneTimeToken.getTokenValue();
        log.info("magic link: {}", magicLink);
    }
}
