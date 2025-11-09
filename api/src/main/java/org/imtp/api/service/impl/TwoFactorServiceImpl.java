package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.config.redis.RedisWrapper;
import org.imtp.api.config.security.AuthProperties;
import org.imtp.api.domain.entity.User;
import org.imtp.api.domain.entity.UserTwoFactor;
import org.imtp.api.enums.TwoFactorType;
import org.imtp.api.mapper.UserTwoFactorMapper;
import org.imtp.api.service.TwoFactorService;
import org.imtp.api.service.TotpService;
import org.imtp.api.service.UserService;
import org.imtp.api.utils.AESUtils;
import org.imtp.api.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.time.Duration;

@Service
@Slf4j
public class TwoFactorServiceImpl extends ServiceImpl<UserTwoFactorMapper, UserTwoFactor> implements TwoFactorService {

    @Resource
    private TotpService totpService;

    @Resource
    private RedisWrapper redisWrapper;

    @Resource
    private UserTwoFactorMapper userTwoFactorMapper;

    @Resource
    private AuthProperties authProperties;

    @Resource
    private UserService userService;

    private final String totp_temp_key_prefix = "totp:temp_secret:";

    @Override
    public String totpSetup(String account) {
        String secret = totpService.createSecret();
        String otpAuthUrl = totpService.buildOtpAuthUrl(account, secret);
        // 暂存secret，验证成功后再写入数据库
        redisWrapper.setValue(totp_temp_key_prefix + account, secret, Duration.ofMinutes(10)); // 10分钟过期
        return otpAuthUrl;
    }

    @Override
    public BufferedImage totpSetupQrcode(String account) {
        String secret = totpService.createSecret();
        String otpAuthUrl = totpService.buildOtpAuthUrl(account, secret);
        BufferedImage bufferedImage = totpService.imageQrcode(otpAuthUrl);
        // 暂存secret，验证成功后再写入数据库
        redisWrapper.setValue(totp_temp_key_prefix + account, secret, Duration.ofMinutes(10)); // 10分钟过期
        return bufferedImage;
    }

    @Override
    public boolean totpVerify(String account, String code) {
        String secret = (String) redisWrapper.getValue(totp_temp_key_prefix + account);
        if (secret == null) {
            throw new BusinessException("No pending TOTP setup for this account");
        }
        int totpCode = Integer.parseInt(code);
        boolean ok = totpVerify(secret, totpCode);
        if (!ok) {
            return false;
        }
        // 验证成功后，将secret写入数据库
        UserTwoFactor userMfa = new UserTwoFactor();
        userMfa.setId(IdGen.genId());
        userMfa.setUserId(SecurityUtils.getCurrentUser(User::getId));
        userMfa.setUsername(account);
        userMfa.setType(TwoFactorType.TOTP);
        try {
            userMfa.setSecret(AESUtils.encrypt(secret, authProperties.getTotp().getSecretKey()));
        } catch (Exception e) {
            log.error("totpVerify error:", e);
            throw new BusinessException("totpVerify error: " + e.getMessage());
        }
        userMfa.setEnabled(true);
        int i = userTwoFactorMapper.insert(userMfa);
        if (i <= 0) {
            throw new BusinessException("Failed to save TOTP secret");
        }
        // 移除缓存的 secret
        redisWrapper.delete(totp_temp_key_prefix + account);
        return true;
    }

    @Override
    public boolean enableTotp(String account, String code) {

        return toggleTotp(account, code, true);
    }

    @Override
    public boolean disableTotp(String account, String code) {

        return toggleTotp(account, code, false);
    }

    private boolean toggleTotp(String account, String code, boolean enable) {
        // 1. 查询用户的 TOTP 记录
        UserTwoFactor record = this.lambdaQuery()
                .select(UserTwoFactor::getId, UserTwoFactor::getSecret, UserTwoFactor::getEnabled)
                .eq(UserTwoFactor::getUsername, account)
                .eq(UserTwoFactor::getType, TwoFactorType.TOTP)
                .one();

        if (record == null) {
            throw new BusinessException("当前账号未绑定 TOTP");
        }
        // 状态一致，无需处理
        if (Boolean.valueOf(enable).equals(record.getEnabled())) {
            return true;
        }
        // 2. 校验 TOTP 动态码格式
        int totpCode = Integer.parseInt(code);
        String secret;
        try {
            // 3. 解密 secret
            secret = AESUtils.decrypt(
                    record.getSecret(),
                    authProperties.getTotp().getSecretKey()
            );
        } catch (Exception e) {
            log.error("toggleTotp error: account={}", account, e);
            throw new BusinessException("totp secret 解密异常");
        }
        // 4. 验证 TOTP 是否正确
        if (!totpVerify(secret, totpCode)) {
            throw new BusinessException("totp令牌不正确");
        }
        // 5. 更新状态
        boolean updated = this.lambdaUpdate()
                .eq(UserTwoFactor::getId, record.getId())
                .set(UserTwoFactor::getEnabled, enable)
                .update();
        if (!updated) {
            throw new BusinessException("更新失败，请稍后再试");
        }
        log.info("TOTP {} success, account={}", enable ? "enabled" : "disabled", account);
        return true;
    }

    private boolean totpVerify(String secret, int code) {
        if (secret == null) {
            throw new BusinessException("No pending TOTP setup for this account");
        }
        return totpService.verifyCode(secret, code);
    }

}
