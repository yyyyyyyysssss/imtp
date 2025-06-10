package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.domain.dto.ChangePasswordDTO;
import org.imtp.api.domain.entity.User;
import org.imtp.api.domain.vo.MenuVO;
import org.imtp.api.service.MenuService;
import org.imtp.api.service.ProfileService;
import org.imtp.api.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/10 9:10
 */
@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    @Resource
    private MenuService menuService;

    @Resource
    private UserService userService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public List<MenuVO> getMenuByUserId(Long userId) {

        return menuService.getMenuByUserId(userId);
    }

    @Override
    public Boolean changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper
                .lambda()
                .select(User::getId, User::getPassword)
                .eq(User::getId, userId);
        User user = userService.getOne(userQueryWrapper);
        if (user == null){
            throw new BusinessException("用户不存在");
        }
        boolean matches = passwordEncoder.matches(changePasswordDTO.getOriginPassword(), user.getPassword());
        if (!matches){
            throw new BusinessException("原密码不正确");
        }
        String newEncodedPassword = passwordEncoder.encode(changePasswordDTO.getNewPassword());
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper
                .lambda()
                .set(User::getPassword, newEncodedPassword)
                .eq(User::getId, userId);
        return userService.update(userUpdateWrapper);
    }
}
