package org.imtp.api.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        String url = "http://localhost:3000/consent" +
                "?client_id=" + clientId +
                "&client_name=" + registeredClient.getClientName() +
                "&scope=" + scope +
                "&state=" + state +
                "&type=" + type +
                "&user_code=" + userCode;
        return "redirect:" + url;
    }

}
