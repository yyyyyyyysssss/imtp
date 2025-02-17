package org.imtp.web.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.enums.ClientType;
import org.imtp.common.response.ResultGenerator;
import org.imtp.web.config.exception.OAuth2ClientLoginException;
import org.imtp.web.config.oauth2.*;
import org.imtp.web.domain.entity.TokenInfo;
import org.imtp.web.domain.entity.User;
import org.imtp.web.domain.vo.*;
import org.imtp.web.utils.WebUtil;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author ys
 * @Date 2025/2/5 13:18
 */

@Service
@Slf4j
public class OAuth2ClientService {


    @Resource
    private RestTemplate oauth2ClientRestTemplate;

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


    public TokenInfo selfLogin(String authorizationCode){
        //获取token
        String tokenUrl = selfProperties.getTokenUrl();
        MultiValueMap<String,String> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("client_id",selfProperties.getClientId());
        bodyMap.add("client_secret",selfProperties.getClientSecret());
        bodyMap.add("redirect_uri",selfProperties.getRedirectUrl());
        bodyMap.add("grant_type","authorization_code");
        bodyMap.add("code",authorizationCode);
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<?> tokenRequestEntity = new HttpEntity<>(bodyMap, tokenHeaders);
        ResponseEntity<SelfTokenVO> tokenResponseEntity = oauth2ClientRestTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                tokenRequestEntity,
                SelfTokenVO.class
        );
        SelfTokenVO selfTokenVO = tokenResponseEntity.getBody();
        log.info("self tokenInfo:{}",selfTokenVO);
        if (selfTokenVO == null){
            throw new OAuth2ClientLoginException("获取self token失败");
        }
        //根据token获取用户信息
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "token " + selfTokenVO.getAccessToken());
        HttpEntity<?> userInfoRequestEntity = new HttpEntity<>(null, headers);
        String userInfoUrl = selfProperties.getUserInfoUrl();
        ResponseEntity<SelfUserInfoVO> userInfoResponseEntity = oauth2ClientRestTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                userInfoRequestEntity,
                SelfUserInfoVO.class
        );
        SelfUserInfoVO selfUserInfoVO = userInfoResponseEntity.getBody();
        log.info("self userInfo:{}", selfUserInfoVO);
        if (selfUserInfoVO == null){
            throw new OAuth2ClientLoginException("获取self 用户信息失败");
        }
        saveUser(selfUserInfoVO);
        UserDetails userDetails = userService.loadUserByUserId(Long.parseLong(selfUserInfoVO.getSub()));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword());
        return loginService.login(authenticationToken);
    }

    public TokenInfo githubLogin(String authorizationCode){
        //获取token
        String accessTokenUrl = githubProperties.getTokenUrl()
                + "?client_id=" + githubProperties.getClientId()
                + "&client_secret=" + githubProperties.getClientSecret()
                + "&code=" + authorizationCode;
        String accessTokenResult = oauth2ClientRestTemplate.getForObject(accessTokenUrl, String.class);
        log.info("github tokenInfo:{}",accessTokenResult);
        Map<String, String> map = WebUtil.requestParamConvertMap(accessTokenResult);
        String accessToken = map.get("access_token");
        //根据token获取用户信息
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "token "+accessToken);
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<GithubUserInfoVO> responseEntity = oauth2ClientRestTemplate.exchange(
                githubProperties.getUserInfoUrl(),
                HttpMethod.GET,
                requestEntity,
                GithubUserInfoVO.class
        );
        GithubUserInfoVO githubUserInfoVO;
        if (!responseEntity.getStatusCode().equals(HttpStatus.OK) || (githubUserInfoVO = responseEntity.getBody()) == null){
            throw new OAuth2ClientLoginException("获取github 用户信息失败");
        }
        //根据token获取用户邮箱
        ResponseEntity<List<GithubUserEmailVO>> githubUserEmailResponse = oauth2ClientRestTemplate.exchange(
                githubProperties.getUserEmailsUrl(),
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );
        List<GithubUserEmailVO> githubUserEmails;
        if (!githubUserEmailResponse.getStatusCode().equals(HttpStatus.OK) || (githubUserEmails = githubUserEmailResponse.getBody()) == null){
            throw new OAuth2ClientLoginException("获取github 用户邮箱失败");
        }
        githubUserInfoVO.setEmail(githubUserEmails.stream().filter(GithubUserEmailVO::getPrimary).map(GithubUserEmailVO::getEmail).findFirst().orElse(null));
        log.info("github userInfo:{}", githubUserInfoVO);
        saveUser(githubUserInfoVO);
        OAuthClientAuthenticationToken oAuthClientAuthenticationToken = new OAuthClientAuthenticationToken(githubUserInfoVO.getId().toString(),null);
        return loginService.login(oAuthClientAuthenticationToken);
    }

    public TokenInfo googleLogin(String authorizationCode, ClientType clientType){
        //获取token
        String tokenUrl = googleProperties.getTokenUrl()
                + "?client_id=" + googleProperties.getClientId()
                + "&client_secret=" + googleProperties.getClientSecret()
                + "&redirect_uri=" + googleProperties.getRedirectUrl(clientType)
                + "&grant_type=authorization_code"
                + "&code=" + authorizationCode;
        HttpEntity<?> tokenRequestEntity = new HttpEntity<>("".getBytes(StandardCharsets.UTF_8), new HttpHeaders());
        ResponseEntity<GoogleTokenVO> tokenResponseEntity = oauth2ClientRestTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                tokenRequestEntity,
                GoogleTokenVO.class
        );
        GoogleTokenVO googleTokenVO = tokenResponseEntity.getBody();
        log.info("google tokenInfo:{}",googleTokenVO);
        if (googleTokenVO == null){
            throw new OAuth2ClientLoginException("获取google token失败");
        }
        //根据token获取用户信息
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + googleTokenVO.getAccessToken());
        HttpEntity<Object> userInfoRequestEntity = new HttpEntity<>(null, headers);
        String userInfoUrl = googleProperties.getUserInfoUrl();
        ResponseEntity<GoogleUserInfoVO> userInfoResponseEntity = oauth2ClientRestTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                userInfoRequestEntity,
                GoogleUserInfoVO.class
        );
        GoogleUserInfoVO googleUserInfoVO = userInfoResponseEntity.getBody();
        log.info("google userInfo:{}", googleUserInfoVO);
        if (googleUserInfoVO == null){
            throw new OAuth2ClientLoginException("获取google 用户信息失败");
        }
        saveUser(googleUserInfoVO);
        OAuthClientAuthenticationToken oAuthClientAuthenticationToken = new OAuthClientAuthenticationToken(googleUserInfoVO.getSub(),null);
        return loginService.login(oAuthClientAuthenticationToken);
    }

    public TokenInfo microsoftLogin(String authorizationCode){
        //获取token
        String tokenUrl = microsoftProperties.getTokenUrl();
        MultiValueMap<String, String> bodyMap= new LinkedMultiValueMap<>();
        bodyMap.add("client_id",microsoftProperties.getClientId());
        bodyMap.add("redirect_uri",microsoftProperties.getRedirectUrl());
        bodyMap.add("grant_type","authorization_code");
        bodyMap.add("code",authorizationCode);
        bodyMap.add("client_secret",microsoftProperties.getClientSecret());
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> tokenRequestEntity = new HttpEntity<>(bodyMap, tokenHeaders);
        ResponseEntity<MicrosoftTokenVO> tokenResponseEntity = oauth2ClientRestTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                tokenRequestEntity,
                MicrosoftTokenVO.class
        );
        MicrosoftTokenVO microsoftTokenVO = tokenResponseEntity.getBody();
        log.info("microsoft tokenInfo:{}",microsoftTokenVO);
        if (microsoftTokenVO == null){
            throw new OAuth2ClientLoginException("获取microsoft token失败");
        }
        //根据token获取用户信息
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + microsoftTokenVO.getAccessToken());
        HttpEntity<Object> userInfoRequestEntity = new HttpEntity<>(null, headers);
        String userInfoUrl = microsoftProperties.getUserInfoUrl();
        ResponseEntity<MicrosoftUserInfoVO> userInfoResponseEntity = oauth2ClientRestTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                userInfoRequestEntity,
                MicrosoftUserInfoVO.class
        );
        MicrosoftUserInfoVO microsoftUserInfoVO = userInfoResponseEntity.getBody();
        log.info("microsoft userInfo:{}", microsoftUserInfoVO);
        if (microsoftUserInfoVO == null){
           throw new OAuth2ClientLoginException("获取microsoft 用户信息失败");
        }
        saveUser(microsoftUserInfoVO);
        OAuthClientAuthenticationToken oAuthClientAuthenticationToken = new OAuthClientAuthenticationToken(microsoftUserInfoVO.getSub(),null);
        return loginService.login(oAuthClientAuthenticationToken);
    }


    private void saveUser(OAuthVO oAuthVO){
        User user = User
                .builder()
                .username(oAuthVO.getUsername())
                .nickname(oAuthVO.getNickname())
                .email(oAuthVO.getEmail())
                .avatar(oAuthVO.getAvatar())
                .build();
        userService.saveOrUpdate(user);
    }


}
