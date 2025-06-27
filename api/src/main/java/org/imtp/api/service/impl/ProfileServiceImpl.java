package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.domain.dto.ChangePasswordDTO;
import org.imtp.api.domain.entity.User;
import org.imtp.api.domain.vo.AuthorityVO;
import org.imtp.api.domain.vo.MenuVO;
import org.imtp.api.domain.vo.RoleVO;
import org.imtp.api.domain.vo.UserInfoVO;
import org.imtp.api.service.*;
import org.imtp.api.utils.TreeUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private AuthorityService authorityService;

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public UserInfoVO userInfo(Long userId) {
        User user = userService.findByUserId(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(user.getId());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setNickname(user.getNickname());
        userInfoVO.setAvatar(user.getAvatar());

        // 用户角色
        List<RoleVO> roles = roleService.findRoleByUserId(userId);
        if (!CollectionUtils.isEmpty(roles)){
            List<String> roleCodes = roles.stream().map(RoleVO::getCode).toList();
            userInfoVO.setRoleCodes(roleCodes);
        }else {
            userInfoVO.setRoleCodes(Collections.emptyList());
        }
        List<Long> roleIds = Optional.ofNullable(roles).orElse(new ArrayList<>()).stream().map(RoleVO::getId).toList();

        // 用户菜单
        List<MenuVO> menus = menuService.findMenuByRoleIds(roleIds);
        if (!CollectionUtils.isEmpty(menus)){
            List<MenuVO> menuTree = TreeUtil.buildTree(
                    menus,
                    MenuVO::getId,
                    MenuVO::getParentId,
                    MenuVO::setChildren,
                    0L
            );
            userInfoVO.setMenuTree(menuTree);
        }else {
            userInfoVO.setMenuTree(Collections.emptyList());
        }

        // 用户权限
        List<AuthorityVO> authorityVOList = authorityService.findAuthorityByRoleIds(roleIds);
        if (!CollectionUtils.isEmpty(authorityVOList)){
            List<String> permissionCodes = authorityVOList.stream().map(AuthorityVO::getCode).distinct().toList();
            userInfoVO.setPermissionCodes(permissionCodes);
        }else {
            userInfoVO.setPermissionCodes(Collections.emptyList());
        }

        return userInfoVO;
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
        if (!passwordEncoder.matches(changePasswordDTO.getOriginPassword(), user.getPassword())){
            throw new BusinessException("原密码不正确");
        }
        if(passwordEncoder.matches(changePasswordDTO.getNewPassword(), user.getPassword())){
            throw new BusinessException("新密码不能与原密码相同");
        }
        String newEncodedPassword = passwordEncoder.encode(changePasswordDTO.getNewPassword());
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper
                .lambda()
                .set(User::getPassword, newEncodedPassword)
                .eq(User::getId, userId);
        return userService.update(userUpdateWrapper);
    }

    @Override
    public Boolean changeAvatar(Long userId, String avatarUrl) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper
                .lambda()
                .select(User::getId)
                .eq(User::getId, userId);
        User user = userService.getOne(userQueryWrapper);
        if (user == null){
            throw new BusinessException("用户不存在");
        }
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper
                .lambda()
                .set(User::getAvatar, avatarUrl)
                .eq(User::getId, userId);
        return userService.update(userUpdateWrapper);
    }
}
