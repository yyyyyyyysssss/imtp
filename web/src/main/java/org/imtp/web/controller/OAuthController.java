package org.imtp.web.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.web.config.oauth2.*;
import org.imtp.web.config.response.Result;
import org.imtp.web.config.response.ResultGenerator;
import org.imtp.web.domain.entity.TokenInfo;
import org.imtp.web.domain.entity.User;
import org.imtp.web.domain.vo.*;
import org.imtp.web.service.LoginService;
import org.imtp.web.service.UserService;
import org.imtp.web.utils.WebUtil;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/27 20:37
 */

@RestController
@RequestMapping("/oauth")
@Slf4j
public class OAuthController {

    @Resource
    private RestTemplate oauthClientRestTemplate;

    @Resource
    private SelfProperties selfProperties;

    @Resource
    private GithubProperties githubProperties;

    @Resource
    private GoogleProperties googleProperties;

    @Resource
    private MicrosoftProperties microsoftProperties;

    @Resource
    private UserService userService;

    @Resource
    private LoginService loginService;

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
        switch (state){
            case "Self" :
                return selfLogin(code);
            case "Github" :
                return githubLogin(code);
            case "Google":
                return googleLogin(code);
            case "Microsoft":
                return microsoftLogin(code);
            default:
                throw new UnsupportedOperationException("no support login method");
        }
    }

    @GetMapping("/self/login")
    public Result<?> selfLogin(@RequestParam("code") String code){
        log.info("self authorization code:{}",code);
        //获取token
        String tokenUrl = selfProperties.getTokenUrl();
        MultiValueMap<String,String> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("client_id",selfProperties.getClientId());
        bodyMap.add("client_secret",selfProperties.getClientSecret());
        bodyMap.add("redirect_uri",selfProperties.getRedirectUrl());
        bodyMap.add("grant_type","authorization_code");
        bodyMap.add("code",code);
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<?> tokenRequestEntity = new HttpEntity<>(bodyMap, tokenHeaders);
        ResponseEntity<SelfTokenVO> tokenResponseEntity = oauthClientRestTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                tokenRequestEntity,
                SelfTokenVO.class
        );
        SelfTokenVO selfTokenVO = tokenResponseEntity.getBody();
        log.info("self tokenInfo:{}",selfTokenVO);
        if (selfTokenVO == null){
            return ResultGenerator.failed("获取self token失败");
        }
        //根据token获取用户信息
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "token " + selfTokenVO.getAccessToken());
        HttpEntity<?> userInfoRequestEntity = new HttpEntity<>(null, headers);
        String userInfoUrl = selfProperties.getUserInfoUrl();
        ResponseEntity<SelfUserInfoVO> userInfoResponseEntity = oauthClientRestTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                userInfoRequestEntity,
                SelfUserInfoVO.class
        );
        SelfUserInfoVO selfUserInfoVO = userInfoResponseEntity.getBody();
        log.info("self userInfo:{}", selfUserInfoVO);
        if (selfUserInfoVO == null){
            return ResultGenerator.failed("获取self 用户信息失败");
        }
        UserDetails userDetails = userService.loadUserByUserId(Long.parseLong(selfUserInfoVO.getSub()));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword());
        TokenInfo tokenInfo = loginService.login(authenticationToken);
        return ResultGenerator.ok(tokenInfo);
    }

    //使用github登录
    @GetMapping("/github/login")
    public Result<?> githubLogin(@RequestParam("code") String code){
        log.info("github authorization code:{}",code);
        //获取token
        String accessTokenUrl = githubProperties.getTokenUrl()
                + "?client_id=" + githubProperties.getClientId()
                + "&client_secret=" + githubProperties.getClientSecret()
                + "&code="+code;
        String accessTokenResult = oauthClientRestTemplate.getForObject(accessTokenUrl, String.class);
        log.info("github tokenInfo:{}",accessTokenResult);
        Map<String, String> map = WebUtil.requestParamConvertMap(accessTokenResult);
        String accessToken = map.get("access_token");
        //根据token获取用户信息
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "token "+accessToken);
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<GithubUserInfoVO> responseEntity = oauthClientRestTemplate.exchange(
                githubProperties.getUserInfoUrl(),
                HttpMethod.GET,
                requestEntity,
                GithubUserInfoVO.class
        );
        GithubUserInfoVO githubUserInfoVO;
        if (!responseEntity.getStatusCode().equals(HttpStatus.OK) || (githubUserInfoVO = responseEntity.getBody()) == null){
            return ResultGenerator.failed("获取github 用户信息失败");
        }
        //根据token获取用户邮箱
        ResponseEntity<List<GithubUserEmailVO>> githubUserEmailResponse = oauthClientRestTemplate.exchange(
                githubProperties.getUserEmailsUrl(),
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );
        List<GithubUserEmailVO> githubUserEmails;
        if (!githubUserEmailResponse.getStatusCode().equals(HttpStatus.OK) || (githubUserEmails = githubUserEmailResponse.getBody()) == null){
            return ResultGenerator.failed("获取github 用户邮箱失败");
        }
        githubUserInfoVO.setEmail(githubUserEmails.stream().filter(GithubUserEmailVO::getPrimary).map(GithubUserEmailVO::getEmail).findFirst().orElse(null));
        log.info("github userInfo:{}", githubUserInfoVO);

        User user = User
                .builder()
                .username(githubUserInfoVO.getId().toString())
                .nickname(githubUserInfoVO.getName())
                .email(githubUserInfoVO.getEmail())
                .avatar(githubUserInfoVO.getAvatarUrl())
                .build();
        userService.save(user);
        OAuthClientAuthenticationToken oAuthClientAuthenticationToken = new OAuthClientAuthenticationToken(githubUserInfoVO.getId().toString(),null);
        TokenInfo tokenInfo = loginService.login(oAuthClientAuthenticationToken);
        return ResultGenerator.ok(tokenInfo);
    }

    //使用google登录
    @GetMapping("/google/login")
    public Result<?> googleLogin(@RequestParam("code") String code) {
        log.info("google authorization code:{}",code);
        //获取token
        String tokenUrl = googleProperties.getTokenUrl()
                + "?client_id=" + googleProperties.getClientId()
                + "&client_secret=" + googleProperties.getClientSecret()
                + "&redirect_uri=" + googleProperties.getRedirectUrl()
                + "&grant_type=authorization_code"
                + "&code=" + code;
        HttpEntity<?> tokenRequestEntity = new HttpEntity<>("".getBytes(StandardCharsets.UTF_8), new HttpHeaders());
        ResponseEntity<GoogleTokenVO> tokenResponseEntity = oauthClientRestTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                tokenRequestEntity,
                GoogleTokenVO.class
        );
        GoogleTokenVO googleTokenVO = tokenResponseEntity.getBody();
        log.info("google tokenInfo:{}",googleTokenVO);
        if (googleTokenVO == null){
            return ResultGenerator.failed("获取google token失败");
        }
        //根据token获取用户信息
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + googleTokenVO.getAccessToken());
        HttpEntity<Object> userInfoRequestEntity = new HttpEntity<>(null, headers);
        String userInfoUrl = googleProperties.getUserInfoUrl();
        ResponseEntity<GoogleUserInfoVO> userInfoResponseEntity = oauthClientRestTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                userInfoRequestEntity,
                GoogleUserInfoVO.class
        );
        GoogleUserInfoVO googleUserInfoVO = userInfoResponseEntity.getBody();
        log.info("google userInfo:{}", googleUserInfoVO);
        if (googleUserInfoVO == null){
            return ResultGenerator.failed("获取google 用户信息失败");
        }
        User user = User
                .builder()
                .username(googleUserInfoVO.getSub())
                .nickname(googleUserInfoVO.getName())
                .email(googleUserInfoVO.getEmail())
                .avatar(googleUserInfoVO.getPicture())
                .build();
        userService.save(user);
        OAuthClientAuthenticationToken oAuthClientAuthenticationToken = new OAuthClientAuthenticationToken(googleUserInfoVO.getSub(),null);
        TokenInfo tokenInfo = loginService.login(oAuthClientAuthenticationToken);
        return ResultGenerator.ok(tokenInfo);
    }

    //使用microsoft登录
    @GetMapping("/microsoft/login")
    public Result<?> microsoftLogin(@RequestParam("code") String code) {
        log.info("microsoft authorization code:{}",code);
        //获取token
        String tokenUrl = microsoftProperties.getTokenUrl();
        MultiValueMap<String, String> bodyMap= new LinkedMultiValueMap<>();
        bodyMap.add("client_id",microsoftProperties.getClientId());
        bodyMap.add("redirect_uri",microsoftProperties.getRedirectUrl());
        bodyMap.add("grant_type","authorization_code");
        bodyMap.add("code",code);
        bodyMap.add("client_secret",microsoftProperties.getClientSecret());
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> tokenRequestEntity = new HttpEntity<>(bodyMap, tokenHeaders);
        ResponseEntity<MicrosoftTokenVO> tokenResponseEntity = oauthClientRestTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                tokenRequestEntity,
                MicrosoftTokenVO.class
        );
        MicrosoftTokenVO microsoftTokenVO = tokenResponseEntity.getBody();
        log.info("microsoft tokenInfo:{}",microsoftTokenVO);
        if (microsoftTokenVO == null){
            return ResultGenerator.failed("获取microsoft token失败");
        }
        //根据token获取用户信息
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + microsoftTokenVO.getAccessToken());
        HttpEntity<Object> userInfoRequestEntity = new HttpEntity<>(null, headers);
        String userInfoUrl = microsoftProperties.getUserInfoUrl();
        ResponseEntity<MicrosoftUserInfoVO> userInfoResponseEntity = oauthClientRestTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                userInfoRequestEntity,
                MicrosoftUserInfoVO.class
        );
        MicrosoftUserInfoVO microsoftUserInfoVO = userInfoResponseEntity.getBody();
        log.info("microsoft userInfo:{}", microsoftUserInfoVO);
        if (microsoftUserInfoVO == null){
            return ResultGenerator.failed("获取microsoft 用户信息失败");
        }
        User user = User
                .builder()
                .username(microsoftUserInfoVO.getSub())
                .nickname(microsoftUserInfoVO.getName())
                .email(microsoftUserInfoVO.getEmail())
                .avatar(microsoftUserInfoVO.getPicture())
                .build();
        userService.save(user);
        OAuthClientAuthenticationToken oAuthClientAuthenticationToken = new OAuthClientAuthenticationToken(microsoftUserInfoVO.getSub(),null);
        TokenInfo tokenInfo = loginService.login(oAuthClientAuthenticationToken);
        return ResultGenerator.ok(tokenInfo);
    }

}
