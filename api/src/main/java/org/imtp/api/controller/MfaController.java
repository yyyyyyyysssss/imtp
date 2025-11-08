package org.imtp.api.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.imtp.api.domain.dto.TotpSetupDTO;
import org.imtp.api.domain.entity.User;
import org.imtp.api.domain.vo.TotpSetupVO;
import org.imtp.api.service.MfaService;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;

@RestController
@RequestMapping("/api/mfa")
public class MfaController extends BaseController {

    @Resource
    private MfaService mfaService;

    @GetMapping("/totp/setup")
    public Result<TotpSetupVO> totpSetup(){
        String username = getCurrentUser(User::getUsername);
        TotpSetupVO totpSetupVO = mfaService.totpSetup(username);
        return ResultGenerator.ok(totpSetupVO);
    }

    @GetMapping("/totp/setup/qrcode")
    public void totpSetupQrcode(HttpServletResponse response) throws Exception {
        String username = getCurrentUser(User::getUsername);
        BufferedImage bufferedImage = mfaService.totpSetupQrcode(username);
        writeImage(response.getOutputStream(),bufferedImage);
    }

    @PostMapping("/totp/verify")
    public Result<Boolean> totpVerify(@RequestBody @Validated TotpSetupDTO totpSetupDTO){
        String username = getCurrentUser(User::getUsername);
        boolean ok = mfaService.totpVerify(username, totpSetupDTO.getCode());
        return ResultGenerator.ok(ok);
    }


}
