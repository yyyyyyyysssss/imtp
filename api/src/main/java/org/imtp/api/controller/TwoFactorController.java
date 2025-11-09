package org.imtp.api.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.imtp.api.domain.dto.TotpSetupDTO;
import org.imtp.api.domain.entity.User;
import org.imtp.api.service.TwoFactorService;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;

@RestController
@RequestMapping("/api/2fa")
public class TwoFactorController extends BaseController {

    @Resource
    private TwoFactorService twoFactorService;

    @GetMapping("/totp/setup/url")
    public Result<String> totpSetupUrl(){
        String username = getCurrentUser(User::getUsername);
        String otpAuthUrl = twoFactorService.totpSetup(username);
        return ResultGenerator.ok(otpAuthUrl);
    }

    @GetMapping("/totp/setup/qrcode")
    public void totpSetupQrcode(HttpServletResponse response) throws Exception {
        String username = getCurrentUser(User::getUsername);
        BufferedImage bufferedImage = twoFactorService.totpSetupQrcode(username);
        writeImage(response.getOutputStream(),bufferedImage);
    }

    @PostMapping("/totp/verify")
    public Result<Boolean> totpVerify(@RequestBody @Validated TotpSetupDTO totpSetupDTO){
        String username = getCurrentUser(User::getUsername);
        boolean ok = twoFactorService.totpVerify(username, totpSetupDTO.getCode());
        return ResultGenerator.ok(ok);
    }

    @PostMapping("/totp/enable")
    public Result<Boolean> enableTotp(@RequestBody @Validated TotpSetupDTO totpSetupDTO){
        String username = getCurrentUser(User::getUsername);
        boolean ok = twoFactorService.enableTotp(username, totpSetupDTO.getCode());
        return ResultGenerator.ok(ok);
    }

    @PostMapping("/totp/disable")
    public Result<Boolean> disableTotp(@RequestBody @Validated TotpSetupDTO totpSetupDTO){
        String username = getCurrentUser(User::getUsername);
        boolean ok = twoFactorService.disableTotp(username, totpSetupDTO.getCode());
        return ResultGenerator.ok(ok);
    }

}
