package org.imtp.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.web.config.RequestUrlAuthority;
import org.imtp.web.config.idwork.IdGen;
import org.imtp.web.domain.entity.Authority;
import org.imtp.web.domain.entity.Role;
import org.imtp.web.domain.entity.User;
import org.imtp.web.mapper.AuthorityMapper;
import org.imtp.web.mapper.RoleMapper;
import org.imtp.web.mapper.UserMapper;
import org.imtp.web.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
    public boolean saveOrUpdate(User user) {
        User u = findByUsername(user.getUsername());
        if (u == null){
            user.setId(IdGen.genId());
            user.setCreateTime(new Date());
            return userMapper.insert(user) > 0;
        }else {
            user.setId(u.getId());
            user.setCreateTime(u.getCreateTime());
            return userMapper.updateById(user) > 0;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Wrapper<User> queryWrapper = new QueryWrapper<User>()
                .eq("username", username)
                .or()
                .eq("email",username)
                .or()
                .eq("phone",username);
        User user = userMapper.selectOne(queryWrapper);
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
            user.setAuthorities(new ArrayList<RequestUrlAuthority>());
            return user;
        }
        List<Long> roleIds = roles.stream().map(Role::getId).toList();
        List<Authority> authorities = authorityMapper.findAuthorityByRoleIds(roleIds);
        if (authorities == null || authorities.isEmpty()){
            user.setAuthorities(new ArrayList<RequestUrlAuthority>());
        }else {
            List<RequestUrlAuthority> requestUrlAuthorities = authorities.stream().map(m -> new RequestUrlAuthority(m.getCode(), m.getUrls())).toList();
            user.setAuthorities(requestUrlAuthorities);
        }
        return user;
    }

    @Override
    public User findByUsername(String username) {
        Wrapper<User> queryWrapper = new QueryWrapper<User>().eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User findByUserId(String userId) {
        return userMapper.selectById(userId);
    }
}
