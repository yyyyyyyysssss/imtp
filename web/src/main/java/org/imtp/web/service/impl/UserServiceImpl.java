package org.imtp.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.web.config.RequestUrlAuthority;
import org.imtp.web.domain.entity.Authority;
import org.imtp.web.domain.entity.Role;
import org.imtp.web.domain.entity.User;
import org.imtp.web.mapper.AuthorityMapper;
import org.imtp.web.mapper.RoleMapper;
import org.imtp.web.mapper.UserMapper;
import org.imtp.web.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2023/7/17 11:04
 */
@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private AuthorityMapper authorityMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException("用户不存在");
        }
        return userDetails(user);
    }

    @Override
    public UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException {
        User user = userMapper.selectById(userId);
        if (user == null){
            throw new UsernameNotFoundException("用户不存在");
        }
        return userDetails(user);
    }

    private UserDetails userDetails(User user){
        List<Role> roles = roleMapper.findRoleByUserIds(Collections.singleton(user.getId()));
        if (roles == null || roles.isEmpty()){
            return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(), new ArrayList<RequestUrlAuthority>());
        }
        List<Long> roleIds = roles.stream().map(Role::getId).toList();
        List<Authority> authorities = authorityMapper.findAuthorityByRoleIds(roleIds);
        if (authorities == null || authorities.isEmpty()){
            return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(), new ArrayList<RequestUrlAuthority>());
        }
        List<RequestUrlAuthority> requestUrlAuthorities = authorities.stream().map(m -> new RequestUrlAuthority(m.getCode(), m.getUrls())).toList();
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(), requestUrlAuthorities);
    }

    @Override
    public User findByUsername(String username) {
        Wrapper<User> queryWrapper = new QueryWrapper<User>().eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }
}
