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
import org.imtp.api.domain.entity.User;
import org.imtp.api.domain.entity.UserRole;
import org.imtp.api.domain.vo.RoleVO;
import org.imtp.api.domain.vo.UserCreateVO;
import org.imtp.api.domain.vo.UserVO;
import org.imtp.api.mapper.UserMapper;
import org.imtp.api.mapping.UserMapping;
import org.imtp.api.service.*;
import org.imtp.api.utils.AvatarGeneratorUtils;
import org.imtp.api.utils.PasswordGeneratorUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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
    private RoleService roleService;

    @Resource
    private TenantService tenantService;

    @Resource
    private AuthorityService authorityService;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private RoleAuthorityService roleAuthorityService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private FileService fileService;

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
        User user = checkAndResult(userId);
        return userDetails(user);
    }

    private UserDetails userDetails(User user) {
        List<Long> roleIds = userRoleService.findRoleIdByUserId(user.getId());
        if(CollectionUtils.isEmpty(roleIds)){
            user.setAuthorities(new ArrayList<RequestUrlAuthority>());
            return user;
        }
        List<Long> authorityIds = roleAuthorityService.findAuthorityIdByRoleId(roleIds);
        if (CollectionUtils.isEmpty(authorityIds)) {
            user.setAuthorities(new ArrayList<RequestUrlAuthority>());
            return user;
        }
        List<Authority> authorities = authorityService.listByIds(authorityIds);
        List<RequestUrlAuthority> requestUrlAuthorities = authorities.stream().map(m -> new RequestUrlAuthority(m.getCode(), m.getUrls())).collect(Collectors.toList());
        user.setAuthorities(requestUrlAuthorities);
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
    public List<UserVO> findByUserId(Collection<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            log.warn("findByUserId called with empty userIds");
            return Collections.emptyList();
        }
        return userMapper.selectBatchIds(userIds).stream()
                .map(UserMapping.INSTANCE::toUserVO)
                .collect(Collectors.toList());
    }

    // 根据角色查询用户
    @Override
    public List<UserVO> findByRoleId(Long roleId) {
        if (roleId == null) {
            log.warn("findByRoleId called with null roleId");
            return Collections.emptyList();
        }
        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper
                .lambda()
                .eq(UserRole::getRoleId, roleId);
        List<UserRole> userRoles = userRoleService.list(userRoleQueryWrapper);
        Set<Long> userIds = userRoles.stream().map(UserRole::getUserId).collect(Collectors.toSet());
        return this.findByUserId(userIds);
    }

    @Override
    @Transactional
    public UserCreateVO createUser(UserCreateDTO userCreateDTO) {
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
        if(userCreateDTO.getAvatar() == null || userCreateDTO.getAvatar().isEmpty()){
            String defaultAvatar = generateDefaultAvatar(user.getNickname());
            user.setAvatar(defaultAvatar);
        }
        int row = userMapper.insert(user);
        if (row <= 0) {
            throw new BusinessException("创建用户失败");
        }
        if(userCreateDTO.getRoleIds() != null && !userCreateDTO.getRoleIds().isEmpty()){
            bindRoles(user.getId(), userCreateDTO.getRoleIds());
        }
        userCreateVO.setId(user.getId());
        return userCreateVO;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user:role", key = "#userUpdateDTO.getId()"),
            @CacheEvict(value = "user:authority", key = "#userUpdateDTO.getId()"),
            @CacheEvict(value = "user:menu", key = "#userUpdateDTO.getId()"),
    })
    public Boolean updateUser(UserUpdateDTO userUpdateDTO, Boolean isFullUpdate) {
        User user = checkAndResult(userUpdateDTO.getId());
        if(isFullUpdate){
            UserMapping.INSTANCE.overwriteUser(userUpdateDTO, user);
        } else {
            UserMapping.INSTANCE.updateUser(userUpdateDTO, user);
        }
        int i = userMapper.updateById(user);
        if (i <= 0) {
            throw new BusinessException("更新用户失败");
        }
        if(isFullUpdate){
            bindRoles(user.getId(), userUpdateDTO.getRoleIds());
        } else {
            if(!CollectionUtils.isEmpty(userUpdateDTO.getRoleIds())){
                bindRoles(user.getId(), userUpdateDTO.getRoleIds());
            }
        }
        return true;
    }

    private String generateDefaultAvatar(String name){
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()){
            BufferedImage bufferedImage = AvatarGeneratorUtils.generateAvatar(name);
            ImageIO.write(bufferedImage, "png", os);
            InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
            String filename = UUID.randomUUID().toString().replaceAll("-","");
            return fileService.uploadSingleFile(inputStream, filename + ".png", "image/png");
        }catch (IOException e){
            log.error("生成默认头像失败: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String resetPassword(Long userId) {
        checkAndResult(userId);
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
        return toUserVOPageInfo(PageInfo.of(users));
    }

    @Override
    public UserVO details(Long id) {
        User user = checkAndResult(id);
        UserVO userVO = UserMapping.INSTANCE.toUserVO(user);
        // 查询用户对应的角色
        List<RoleVO> roles = roleService.findByUserId(id);
        if(!CollectionUtils.isEmpty(roles)){
            List<Long> roleIds = roles.stream().map(RoleVO::getId).toList();
            userVO.setRoleIds(roleIds);
        }
        return userVO;
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
        return toUserVOPageInfo(PageInfo.of(users));
    }

    private PageInfo<UserVO> toUserVOPageInfo(PageInfo<User> userPageInfo){
        List<User> users = userPageInfo.getList();
        if (users == null || users.isEmpty()) {
            return new PageInfo<>();
        }
        List<UserVO> result = UserMapping.INSTANCE.toUserVO(users);
        PageInfo<UserVO> pageInfo = new PageInfo<>();
        pageInfo.setList(result);
        pageInfo.setTotal(userPageInfo.getTotal());
        pageInfo.setPageNum(userPageInfo.getPageNum());
        pageInfo.setPageSize(userPageInfo.getPageSize());
        return pageInfo;
    }

    @Override
    public Boolean deleteUser(Long id) {
        int i = userMapper.deleteById(id);
        if(i <= 0){
            throw new BusinessException("删除用户失败，用户不存在");
        }
        // 解绑用户对应的角色
        roleService.unbindUserRoles(id);
        // 解绑用户对应的租户
        tenantService.unbindUserTenant(id);
        return true;
    }

    @Override
    @Transactional
    public Boolean bindRoles(Long id, List<Long> roleIds) {

        return roleService.bindUserRole(id, roleIds);
    }

    private User checkAndResult(Long id){
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public List<UserVO> listUserOptions(){
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper
                .lambda()
                .select(User::getId, User::getNickname)
                .eq(User::isEnabled, true)
                .orderByDesc(User::getCreateTime);
        List<User> users = userMapper.selectList(userQueryWrapper);
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        return UserMapping.INSTANCE.toUserVO(users);
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
