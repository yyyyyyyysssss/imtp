package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.imtp.api.domain.entity.UserTwoFactor;

import java.awt.image.BufferedImage;

public interface TwoFactorService extends IService<UserTwoFactor> {


    String totpSetup(String account);

    BufferedImage totpSetupQrcode(String account);

    boolean totpVerify(String account, String code);

    boolean enableTotp(String account, String code);

    boolean disableTotp(String account, String code);

}
