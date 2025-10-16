package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.config.security.RequestUrlAuthority;
import org.imtp.api.domain.dto.UserCreateDTO;
import org.imtp.api.domain.dto.UserQueryDTO;
import org.imtp.api.domain.dto.UserUpdateDTO;
import org.imtp.api.domain.entity.Authority;
import org.imtp.api.domain.entity.Role;
import org.imtp.api.domain.entity.User;
import org.imtp.api.domain.entity.UserRole;
import org.imtp.api.domain.vo.UserCreateVO;
import org.imtp.api.domain.vo.UserVO;
import org.imtp.api.mapper.AuthorityMapper;
import org.imtp.api.mapper.RoleMapper;
import org.imtp.api.mapper.UserMapper;
import org.imtp.api.mapping.UserMapping;
import org.imtp.api.service.UserRoleService;
import org.imtp.api.service.UserService;
import org.imtp.api.utils.PasswordGeneratorUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author ys
 * @Date 2023/7/17 11:04
 */
@Service("userService")
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private AuthorityMapper authorityMapper;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean saveOrUpdate(User user) {
        User u = findByUsername(user.getUsername());
        if (u == null) {
            user.setId(IdGen.genId());
            user.setCreateTime(new Date());
            return userMapper.insert(user) > 0;
        } else {
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
                .eq("email", username)
                .or()
                .eq("phone", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return userDetails(user);
    }

    @Override
    public UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return userDetails(user);
    }

    private UserDetails userDetails(User user) {
        List<Role> roles = roleMapper.findRoleByUserIds(Collections.singleton(user.getId()));
        if (roles == null || roles.isEmpty()) {
            user.setAuthorities(new ArrayList<RequestUrlAuthority>());
            return user;
        }
        List<Long> roleIds = roles.stream().map(Role::getId).toList();
        List<Authority> authorities = authorityMapper.findAuthorityByRoleIds(roleIds);
        if (authorities == null || authorities.isEmpty()) {
            user.setAuthorities(new ArrayList<RequestUrlAuthority>());
        } else {
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
    public User findByUserId(Serializable userId) {
        return userMapper.selectById(userId);
    }

    @Override
    @Transactional
    public UserCreateVO create(UserCreateDTO userCreateDTO) {
        User user = UserMapping.INSTANCE.toUser(userCreateDTO);
        user.setId(IdGen.genId());
        UserCreateVO userCreateVO = new UserCreateVO();
        String password;
        if(user.getPassword() == null || user.getPassword().isEmpty()){
            password = PasswordGeneratorUtils.generate(10);
            userCreateVO.setInitialPassword(password);
        }else {
            password = user.getPassword();
        }
        String encryptPassword = passwordEncoder.encode(password);
        user.setPassword(encryptPassword);
        int row = userMapper.insert(user);
        if (row <= 0) {
            throw new BusinessException("创建用户失败");
        }
        if(userCreateDTO.getRoleIds() != null && !userCreateDTO.getRoleIds().isEmpty()){
            addUserRole(user.getId(), userCreateDTO.getRoleIds());
        }
        userCreateVO.setId(user.getId());
        return userCreateVO;
    }

    @Override
    @Transactional
    public Integer update(UserUpdateDTO userUpdateDTO) {
        User user = userMapper.selectById(userUpdateDTO.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserMapping.INSTANCE.overwriteUser(userUpdateDTO, user);
        int i = userMapper.updateById(user);
        if (i <= 0) {
            throw new BusinessException("更新用户失败");
        }
        addUserRole(user.getId(), userUpdateDTO.getRoleIds());
        return i;
    }

    @Override
    @Transactional
    public Integer updatePartial(UserUpdateDTO userUpdateDTO) {
        User user = userMapper.selectById(userUpdateDTO.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserMapping.INSTANCE.updateUser(userUpdateDTO, user);
        int i = userMapper.updateById(user);
        if (i <= 0) {
            throw new BusinessException("更新用户失败");
        }
        if(!CollectionUtils.isEmpty(userUpdateDTO.getRoleIds())){
            addUserRole(user.getId(), userUpdateDTO.getRoleIds());
        }
        return i;
    }

    @Override
    public String resetPassword(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        String newPassword = PasswordGeneratorUtils.generate(10);
        String encryptPassword = passwordEncoder.encode(newPassword);
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.lambda().eq(User::getId,userId).set(User::getPassword,encryptPassword);
        int update = userMapper.update(null, userUpdateWrapper);
        if (update <= 0){
            throw new BusinessException("密码重置失败");
        }
        return newPassword;
    }

    @Override
    public PageInfo<UserVO> queryList(UserQueryDTO queryDTO) {
        Integer pageNum = queryDTO.getPageNum();
        Integer pageSize = queryDTO.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<User> userQueryWrapper = getUserQueryWrapper(queryDTO);
        List<User> users = userMapper.selectList(userQueryWrapper);
        if (users == null || users.isEmpty()) {
            return new PageInfo<>();
        }
        PageInfo<User> userPageInfo = PageInfo.of(users);

        List<Long> userIds = users.stream().map(User::getId).toList();
        // 查询用户对应的角色
        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper
                .lambda()
                .in(UserRole::getUserId, userIds);
        List<UserRole> userRoles = userRoleService.list(userRoleQueryWrapper);
        Map<Long, List<Long>> userRoleIdMap = userRoles.stream().collect(Collectors.groupingBy(
                UserRole::getUserId,
                Collectors.mapping(UserRole::getRoleId, Collectors.toList()
                )));
        List<UserVO> result = new ArrayList<>();
        for (User user : users) {
            UserVO userVO = UserMapping.INSTANCE.toUserVO(user);
            List<Long> roleIds = userRoleIdMap.getOrDefault(user.getId(), new ArrayList<>());
            userVO.setRoleIds(roleIds);
            result.add(userVO);
        }
        PageInfo<UserVO> pageInfo = new PageInfo<>();
        pageInfo.setList(result);
        pageInfo.setTotal(userPageInfo.getTotal());
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    @Override
    public PageInfo<UserVO> search(Integer pageNum,Integer pageSize,String name,List<Long> ids){
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper
                .lambda()
                .select(User::getId, User::getNickname)
                .eq(User::isEnabled, true)
                .orderByDesc(User::getCreateTime);
        if (name != null && !name.isEmpty()) {
            userQueryWrapper.lambda().like(User::getNickname, name);
        }
        if(ids != null && !ids.isEmpty()){
            pageSize = ids.size();
            userQueryWrapper.lambda().in(User::getId, ids);
        }
        PageHelper.startPage(pageNum, pageSize);
        List<User> users = userMapper.selectList(userQueryWrapper);
        if (users == null || users.isEmpty()) {
            return new PageInfo<>();
        }
        PageInfo<User> userPageInfo = PageInfo.of(users);
        List<UserVO> userVO = UserMapping.INSTANCE.toUserVO(users);
        PageInfo<UserVO> pageInfo = new PageInfo<>();
        pageInfo.setList(userVO);
        pageInfo.setTotal(userPageInfo.getTotal());
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    @Override
    public Integer delete(String id) {
        int i = userMapper.deleteById(id);
        if (i > 0){
            QueryWrapper<UserRole> roleAuthorityQueryWrapper = new QueryWrapper<>();
            roleAuthorityQueryWrapper
                    .lambda()
                    .eq(UserRole::getUserId, id);
            // 删除角色对应的权限
            userRoleService.remove(roleAuthorityQueryWrapper);
        }else {
            throw new BusinessException("删除用户失败，用户可能不存在");
        }
        return i;
    }

    @Override
    @Transactional
    public Boolean bindRoles(Long id, List<Long> roleIds) {

        return addUserRole(id,roleIds);
    }

    @Transactional
    public boolean addUserRole(Long userId, Collection<Long> roleIds) {
        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper
                .lambda()
                .eq(UserRole::getUserId, userId);
        // 删除原有的角色权限
        userRoleService.remove(userRoleQueryWrapper);
        if (CollectionUtils.isEmpty(roleIds)){
            return true;
        }
        // 添加新的角色权限
        return userRoleService.buildUserRoles(Collections.singleton(userId), roleIds);
    }

    private QueryWrapper<User> getUserQueryWrapper(UserQueryDTO userQueryDTO) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (userQueryDTO.getKeyword() != null && !userQueryDTO.getKeyword().isEmpty()) {
            userQueryWrapper
                    .lambda()
                    .like(User::getUsername, userQueryDTO.getKeyword())
                    .or()
                    .like(User::getNickname, userQueryDTO.getKeyword())
                    .or()
                    .like(User::getEmail, userQueryDTO.getKeyword())
                    .or()
                    .like(User::getPhone, userQueryDTO.getKeyword());
        }
        if (userQueryDTO.getEnabled() != null) {
            userQueryWrapper.eq("enabled", userQueryDTO.getEnabled());
        }
        userQueryWrapper.orderByDesc("create_time");
        return userQueryWrapper;
    }
}
