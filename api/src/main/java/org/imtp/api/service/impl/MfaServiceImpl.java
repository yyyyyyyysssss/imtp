package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.config.redis.RedisWrapper;
import org.imtp.api.config.security.AuthProperties;
import org.imtp.api.domain.entity.User;
import org.imtp.api.domain.entity.UserMfa;
import org.imtp.api.domain.vo.TotpSetupVO;
import org.imtp.api.enums.MfaType;
import org.imtp.api.mapper.UserMfaMapper;
import org.imtp.api.service.MfaService;
import org.imtp.api.service.TotpService;
import org.imtp.api.service.UserService;
import org.imtp.api.utils.AESUtils;
import org.imtp.api.utils.RSAUtils;
import org.imtp.api.utils.SecurityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.Base64;

@Service
@Slf4j
public class MfaServiceImpl extends ServiceImpl<UserMfaMapper, UserMfa> implements MfaService {

    @Resource
    private TotpService totpService;

    @Resource
    private RedisWrapper redisWrapper;

    @Resource
    private UserMfaMapper userMfaMapper;

    @Resource
    private AuthProperties authProperties;

    @Resource
    private UserService userService;

    private final String totp_temp_key_prefix = "totp:temp_secret:";

    @Override
    public TotpSetupVO totpSetup(String account){
        String secret = totpService.createSecret();
        String otpAuthUrl = totpService.buildOtpAuthUrl(account, secret);
        byte[] qrBytes = totpService.bytesQrcode(otpAuthUrl);
        String qrBase64 = "data:image/png;base64," +
                Base64.getEncoder().encodeToString(qrBytes);
        // 暂存secret，验证成功后再写入数据库
        redisWrapper.setValue(totp_temp_key_prefix + account, secret, Duration.ofMinutes(10)); // 10分钟过期
        return new TotpSetupVO(qrBase64, otpAuthUrl);
    }

    @Override
    public BufferedImage totpSetupQrcode(String account){
        String secret = totpService.createSecret();
        String otpAuthUrl = totpService.buildOtpAuthUrl(account, secret);
        BufferedImage bufferedImage = totpService.imageQrcode(otpAuthUrl);
        // 暂存secret，验证成功后再写入数据库
        redisWrapper.setValue(totp_temp_key_prefix + account, secret, Duration.ofMinutes(10)); // 10分钟过期
        return bufferedImage;
    }

    @Override
    public boolean totpVerify(String account, String code){
        String secret = (String)redisWrapper.getValue(totp_temp_key_prefix + account);
        if (secret == null) {
            throw new BusinessException("No pending TOTP setup for this account");
        }
        boolean ok = totpService.verifyCode(secret, code);
        if(!ok){
            return false;
        }
        // 验证成功后，将secret写入数据库
        UserMfa userMfa = new UserMfa();
        userMfa.setId(IdGen.genId());
        userMfa.setUserId(SecurityUtils.getCurrentUser(User::getId));
        userMfa.setUsername(account);
        userMfa.setMfaType(MfaType.TOTP);
        try {
            userMfa.setSecret(AESUtils.encrypt(secret,authProperties.getTotp().getSecretKey()));
        } catch (Exception e) {
            log.error("totpVerify error:",e);
            throw new BusinessException("totpVerify error: " + e.getMessage());
        }
        userMfa.setEnabled(true);
        int i = userMfaMapper.insert(userMfa);
        if(i <= 0){
            throw new BusinessException("Failed to save TOTP secret");
        }
        // 移除缓存的 secret
        redisWrapper.delete(totp_temp_key_prefix + account);
        return true;
    }

}
