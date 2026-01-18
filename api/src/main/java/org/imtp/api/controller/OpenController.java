package org.imtp.api.controller;

import groovy.lang.Tuple2;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.imtp.api.config.webdav.WebDavClient;
import org.imtp.api.utils.*;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.imtp.api.config.security.authentication.email.EmailAuthenticationProvider;
import org.imtp.api.domain.dto.EmailInfo;
import org.imtp.api.domain.vo.TokenValidVO;
import org.imtp.api.enums.TokenType;
import org.imtp.api.service.EmailService;
import org.imtp.api.config.security.TokenService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 对外提供无需身份认证和授权的接口
 * @Author ys
 * @Date 2024/12/5 10:01
 */
@RestController
@RequestMapping("/api/open")
public class OpenController extends BaseController{

    @Resource
    private TokenService tokenService;

    @Resource
    private EmailService emailService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/tokenValid")
    public Result<TokenValidVO> tokenValid(@RequestParam("token") String token, @RequestParam("tokenType") TokenType tokenType){
        Tuple2<Boolean, PayloadInfo> valid = tokenService.isValid(token, tokenType);
        TokenValidVO tokenValidVO = new TokenValidVO();
        tokenValidVO.setActive(valid.getV1());
        if (valid.getV1()) {
            PayloadInfo payloadInfo = valid.getV2();
            tokenValidVO.setSubject(payloadInfo.getSubject());
            tokenValidVO.setClientType(payloadInfo.getClientType());
            tokenValidVO.setExpiration(payloadInfo.getExpiration());
        }
        return ResultGenerator.ok(tokenValidVO);
    }

    @GetMapping("/simple/qrcode")
    public void simpleQRCode(@RequestParam("content") String content, HttpServletResponse response) throws IOException {
        BufferedImage qrCodeImage = QrCodeUtils.createQrCodeImage(content);
        writeImage(response.getOutputStream(),qrCodeImage);
    }

    @GetMapping("/captcha/image")
    public void captchaImage(HttpServletResponse response) throws IOException {
        BufferedImage bufferedImage = CaptchaUtils.generateCaptchaImage();
        writeImage(response.getOutputStream(),bufferedImage);
    }

    @GetMapping("/generator/avatar")
    public void generatorAvatar(@RequestParam("content") String content, HttpServletResponse response) throws IOException {
        BufferedImage avatarImage = AvatarGeneratorUtils.generateAvatar(content);
        writeImage(response.getOutputStream(),avatarImage);
    }

    @PostMapping("/merge/avatar")
    public void mergeAvatar(@RequestBody List<String> urls, HttpServletResponse response) throws IOException, URISyntaxException {
        BufferedImage avatarImage = AvatarGeneratorUtils.mergeAvatar(urls);
        writeImage(response.getOutputStream(),avatarImage);
    }

    @GetMapping("/email/send/code")
    public Result<?> sendEmailVerificationCode(@RequestParam("email") String email) {
        EmailInfo emailInfo = EmailInfo
                .builder()
                .title("邮箱验证码")
                .to(new String[]{email})
                .build();
        String verificationCode = VerificationCodeUtils.genVerificationCode();
        redisTemplate.opsForValue().set(EmailAuthenticationProvider.EMAIL_VERIFICATION_CODE_PREFIX + email, verificationCode);
        Map<String, Object> variable = new HashMap<>();
        variable.put("verificationCode", verificationCode);
        emailService.sendHtmlEmail(emailInfo, "EmailVerificationCode", variable);
        return ResultGenerator.ok();
    }

    @Resource
    private WebDavClient webDavClient;

    @GetMapping("/webdev/test")
    public Result<?> webdev() {
        String authorization = webDavClient.getAuthorization();
        return ResultGenerator.ok(authorization);
    }

}
