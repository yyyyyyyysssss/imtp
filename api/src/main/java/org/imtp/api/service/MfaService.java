package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.imtp.api.domain.entity.UserMfa;
import org.imtp.api.domain.vo.TotpSetupVO;

import java.awt.image.BufferedImage;

public interface MfaService extends IService<UserMfa> {


    TotpSetupVO totpSetup(String account);

    BufferedImage totpSetupQrcode(String account);

    boolean totpVerify(String account, String code);

}
