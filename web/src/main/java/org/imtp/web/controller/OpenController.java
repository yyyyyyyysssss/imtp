package org.imtp.web.controller;

import com.google.zxing.WriterException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.imtp.web.config.EmailAuthenticationProvider;
import org.imtp.web.config.RefreshTokenServices;
import org.imtp.web.domain.dto.EmailInfo;
import org.imtp.web.domain.vo.TokenValidVO;
import org.imtp.web.enums.TokenType;
import org.imtp.web.service.EmailService;
import org.imtp.web.service.TokenService;
import org.imtp.web.utils.JwtUtil;
import org.imtp.web.utils.PayloadInfo;
import org.imtp.web.utils.QrCodeUtil;
import org.imtp.web.utils.VerificationCodeUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 对外提供无需身份认证和授权的接口
 * @Author ys
 * @Date 2024/12/5 10:01
 */
@RestController
@RequestMapping("/open")
public class OpenController {

    @Resource
    private TokenService tokenService;

    @Resource
    private EmailService emailService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/tokenValid")
    public Result<TokenValidVO> tokenValid(@RequestParam("token") String token, @RequestParam("tokenType") TokenType tokenType){
        boolean valid = tokenService.isValid(token, tokenType);
        TokenValidVO tokenValidVO = new TokenValidVO();
        tokenValidVO.setActive(valid);
        if (valid) {
            if (tokenType.equals(TokenType.ACCESS_TOKEN)){
                PayloadInfo payloadInfo = JwtUtil.extractPayloadInfo(token);
                tokenValidVO.setSubject(payloadInfo.getSubject());
                tokenValidVO.setClientType(payloadInfo.getClientType());
                tokenValidVO.setExpiration(payloadInfo.getExpiration());
            }else {
                RefreshTokenServices.RefreshTokenPayloadInfo refreshTokenPayloadInfo = RefreshTokenServices.extractPayloadInfo(token);
                tokenValidVO.setSubject(refreshTokenPayloadInfo.getSubject());
                tokenValidVO.setClientType(refreshTokenPayloadInfo.getClientType());
                tokenValidVO.setExpiration(refreshTokenPayloadInfo.getExpiration());
            }
        }
        return ResultGenerator.ok(tokenValidVO);
    }

    @GetMapping("/simpleQRCode")
    public void simpleQRCode(@RequestParam("content") String content, HttpServletResponse response) throws IOException {
        BufferedImage qrCodeImage = QrCodeUtil.createQrCodeImage(content);
        QrCodeUtil.writeQrCodeImage(response.getOutputStream(),qrCodeImage);
    }

    @GetMapping("/sendEmailVerificationCode")
    public Result<?> sendEmailVerificationCode(@RequestParam("email") String email) {
        EmailInfo emailInfo = EmailInfo
                .builder()
                .title("邮箱验证码")
                .to(new String[]{email})
                .build();
        String verificationCode = VerificationCodeUtil.genVerificationCode();
        redisTemplate.opsForValue().set(EmailAuthenticationProvider.EMAIL_VERIFICATION_CODE_PREFIX + email, verificationCode);
        Map<String, Object> variable = new HashMap<>();
        variable.put("verificationCode", verificationCode);
        emailService.sendHtmlEmail(emailInfo, "EmailVerificationCode", variable);
        return ResultGenerator.ok();
    }

}
