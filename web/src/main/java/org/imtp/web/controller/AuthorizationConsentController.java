package org.imtp.web.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/7 16:46
 */
@Controller
@Slf4j
public class AuthorizationConsentController {

    @Resource
    private RegisteredClientRepository registeredClientRepository;

    @Resource
    private OAuth2AuthorizationConsentService authorizationConsentService;

    //自定义授权同意页面
    @GetMapping(value = "/oauth2/consent")
    public String consent(@RequestParam("type") String type,
                          @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
                          @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
                          @RequestParam(OAuth2ParameterNames.STATE) String state,
                          @RequestParam(value = OAuth2ParameterNames.USER_CODE,required = false) String userCode
    ) {
        log.info("type:{},clientId:{},scope:{},state:{},userCode:{}",type,clientId,scope,state,userCode);
        RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
        StringBuilder url = new StringBuilder();
        url.append("http://localhost:3000/consent");
        url.append("?client_id=").append(clientId);
        url.append("&client_name=").append(registeredClient.getClientName());
        url.append("&scope=").append(scope);
        url.append("&state=").append(state);
        url.append("&type=").append(type);
        url.append("&user_code=").append(userCode);
        return "redirect:" + url;
    }

}
