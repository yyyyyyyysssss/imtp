package org.imtp.web.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.imtp.web.config.oauth2.GithubProperties;
import org.imtp.web.config.oauth2.GoogleProperties;
import org.imtp.web.config.oauth2.MicrosoftProperties;
import org.imtp.web.config.oauth2.SelfProperties;
import org.imtp.web.domain.entity.TokenInfo;
import org.imtp.web.service.OAuth2ClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/27 20:37
 */

@RestController
@RequestMapping("/oauth2/client")
@Slf4j
public class OAuth2ClientController {

    @Resource
    private SelfProperties selfProperties;

    @Resource
    private GithubProperties githubProperties;

    @Resource
    private GoogleProperties googleProperties;

    @Resource
    private MicrosoftProperties microsoftProperties;

    @Resource
    private OAuth2ClientService oAuth2ClientService;

    @GetMapping("/other/config")
    public Result<?> otherConfig(){
        Map<String,Object> config = new HashMap<>();
//        config.put(selfProperties.getClientName(), new OtherConfig(OtherConfig.DEVICE_CODE_TYPE, selfProperties.getDeviceCodeUrl()));
        config.put(selfProperties.getClientName(), new OtherConfig(selfProperties.getAuthCodeUrl()));
        config.put(githubProperties.getClientName(),new OtherConfig(githubProperties.getAuthCodeUrl()));
        config.put(googleProperties.getClientName(),new OtherConfig(googleProperties.getAuthCodeUrl()));
        config.put(microsoftProperties.getClientName(),new OtherConfig(microsoftProperties.getAuthCodeUrl()));
        return ResultGenerator.ok(config);
    }

    static class OtherConfig{
        private static final String AUTH_CODE_TYPE = "auth_code";
        private static final String DEVICE_CODE_TYPE = "device_code";
        public OtherConfig(String url){
            this(AUTH_CODE_TYPE,url);
        }
        public OtherConfig(String type,String url){
            this.type = type;
            this.url = url;
        }
        private String type;
        private String url;
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    @GetMapping("/other/login")
    public Result<?> otherLogin(@RequestParam("code") String code,@RequestParam("state") String state){
        TokenInfo tokenInfo = null;
        switch (state){
            case "Self" :
                tokenInfo = oAuth2ClientService.selfLogin(code);
                break;
            case "Github" :
                tokenInfo = oAuth2ClientService.githubLogin(code);
                break;
            case "Google":
                tokenInfo = oAuth2ClientService.googleLogin(code);
                break;
            case "Microsoft":
                tokenInfo = oAuth2ClientService.microsoftLogin(code);
                break;
            default:
                throw new UnsupportedOperationException("no support login method");
        }
        return ResultGenerator.ok(tokenInfo);
    }

    @GetMapping("/self/login")
    public Result<?> selfLogin(@RequestParam("code") String code){
        log.info("self authorization code:{}",code);
        TokenInfo tokenInfo = oAuth2ClientService.selfLogin(code);
        return ResultGenerator.ok(tokenInfo);
    }

    //使用github登录
    @GetMapping("/github/login")
    public Result<?> githubLogin(@RequestParam("code") String code){
        log.info("github authorization code:{}",code);
        TokenInfo tokenInfo = oAuth2ClientService.githubLogin(code);
        return ResultGenerator.ok(tokenInfo);
    }

    //使用google登录
    @GetMapping("/google/login")
    public Result<?> googleLogin(@RequestParam("code") String code) {
        log.info("google authorization code:{}",code);
        TokenInfo tokenInfo = oAuth2ClientService.googleLogin(code);
        return ResultGenerator.ok(tokenInfo);
    }

    //使用microsoft登录
    @GetMapping("/microsoft/login")
    public Result<?> microsoftLogin(@RequestParam("code") String code) {
        log.info("microsoft authorization code:{}",code);
        TokenInfo tokenInfo = oAuth2ClientService.microsoftLogin(code);
        return ResultGenerator.ok(tokenInfo);
    }

}
