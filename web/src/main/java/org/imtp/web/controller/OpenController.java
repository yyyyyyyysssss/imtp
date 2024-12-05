package org.imtp.web.controller;

import jakarta.annotation.Resource;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.imtp.web.domain.entity.User;
import org.imtp.web.domain.vo.TokenValidVO;
import org.imtp.web.enums.TokenType;
import org.imtp.web.service.TokenService;
import org.imtp.web.service.UserService;
import org.imtp.web.utils.JwtUtil;
import org.imtp.web.utils.PayloadInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private UserService userService;

    @GetMapping("/tokenValid")
    public Result<TokenValidVO> tokenValid(@RequestParam("token") String token, @RequestParam("tokenType") TokenType tokenType){
        boolean valid = tokenService.isValid(token, tokenType);
        TokenValidVO tokenValidVO = new TokenValidVO();
        tokenValidVO.setActive(valid);
        if (valid) {
            String userId = JwtUtil.extractUserId(token);
            User user = userService.findByUserId(userId);
            tokenValidVO.setUserInfo(user);
        }
        return ResultGenerator.ok(tokenValidVO);
    }

}
