package org.imtp.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/30 9:49
 */
@Controller
@Slf4j
public class AuthorizationDeviceController {


    @GetMapping("/oauth2/activate")
    public String activate(@RequestParam(value = "user_code", required = false) String userCode) {
        log.info("user_code:{}",userCode);
        if (userCode != null) {
            return "redirect:/oauth2/device_verification?user_code=" + userCode;
        }
        SecurityContext securityContext;
        if ((securityContext = SecurityContextHolder.getContext()) == null || securityContext.getAuthentication() == null || securityContext.getAuthentication() instanceof AnonymousAuthenticationToken){
            return "redirect:http://localhost:3000/login?target=http://localhost:3000/activate";
        }
        return "redirect:http://localhost:3000/activate";
    }

    @GetMapping("/activated")
    public String activated() {
        return "redirect:http://localhost:3000/activated";
    }

}
