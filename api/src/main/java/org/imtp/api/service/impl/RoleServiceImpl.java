package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.domain.dto.RoleBindUserDTO;
import org.imtp.api.domain.dto.RoleCreateDTO;
import org.imtp.api.domain.dto.RoleQueryDTO;
import org.imtp.api.domain.dto.RoleUpdateDTO;
import org.imtp.api.domain.entity.Role;
import org.imtp.api.domain.entity.RoleAuthority;
import org.imtp.api.domain.entity.UserRole;
import org.imtp.api.domain.vo.AuthorityVO;
import org.imtp.api.domain.vo.RoleVO;
import org.imtp.api.domain.vo.UserVO;
import org.imtp.api.enums.RoleType;
import org.imtp.api.mapper.RoleMapper;
import org.imtp.api.mapping.RoleMapping;
import org.imtp.api.service.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/3 15:36
 */
@Service("roleServiceProxy")
@Slf4j
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>  implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleAuthorityService roleAuthorityService;

    @Resource
    private AuthorityService authorityService;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private UserService userService;

    @Resource(name = "roleServiceProxy")
    private RoleService roleServiceProxy;

    @Override
    @Transactional
    public Long create(RoleCreateDTO roleCreateDTO) {
        Role role = RoleMapping.INSTANCE.toRole(roleCreateDTO);
        role.setId(IdGen.genId());
        role.setType(RoleType.NORMAL);
        int row = roleMapper.insert(role);
        if (row <= 0) {
            throw new BusinessException("创建角色失败");
        }
        if(!CollectionUtils.isEmpty(roleCreateDTO.getUserIds())){
            roleServiceProxy.bindRoleUsers(role.getId(), roleCreateDTO.getUserIds());
        }
        if(!CollectionUtils.isEmpty(roleCreateDTO.getAuthorityIds())){
            roleServiceProxy.bindRoleAuthorities(role.getId(), roleCreateDTO.getAuthorityIds());
        }
        return role.getId();
    }

    @Override
    @Transactional
    public Integer update(RoleUpdateDTO roleUpdateDTO) {
        Role role = checkAndResult(roleUpdateDTO.getId());
        if(role.isSuperAdmin()){
            throw new BusinessException("超级管理员角色无法修改");
        }
        RoleMapping.INSTANCE.overwriteRole(roleUpdateDTO, role);
        int i = roleMapper.updateById(role);
        if (i <= 0) {
            throw new BusinessException("更新角色失败");
        }
        // 更新角色关联的用户
        roleServiceProxy.bindRoleUsers(role.getId(),roleUpdateDTO.getUserIds());
        // 更新角色关联的权限
        roleServiceProxy.bindRoleAuthorities(role.getId(), roleUpdateDTO.getAuthorityIds());
        return i;
    }

    @Override
    @Transactional
    public Integer updatePartial(RoleUpdateDTO roleUpdateDTO) {
        Role role = checkAndResult(roleUpdateDTO.getId());
        if(role.isSuperAdmin()){
            throw new BusinessException("超级管理员角色无法修改");
        }
        RoleMapping.INSTANCE.updateRole(roleUpdateDTO, role);
        int i = roleMapper.updateById(role);
        if (i <= 0) {
            throw new BusinessException("更新角色失败");
        }
        if(!CollectionUtils.isEmpty(roleUpdateDTO.getUserIds())){
            roleServiceProxy.bindRoleUsers(role.getId(),roleUpdateDTO.getUserIds());
        }
        if(!CollectionUtils.isEmpty(roleUpdateDTO.getAuthorityIds())){
            roleServiceProxy.bindRoleAuthorities(role.getId(), roleUpdateDTO.getAuthorityIds());
        }
        return i;
    }

    @Override
    public PageInfo<RoleVO> queryList(RoleQueryDTO queryDTO) {
        Integer pageNum = queryDTO.getPageNum();
        Integer pageSize = queryDTO.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<Role> roleQueryWrapper = getRoleQueryWrapper(queryDTO);
        List<Role> roles = roleMapper.selectList(roleQueryWrapper);
        if (roles == null || roles.isEmpty()) {
            return new PageInfo<>();
        }
        PageInfo<Role> rolePageInfo = PageInfo.of(roles);
        List<RoleVO> result = RoleMapping.INSTANCE.toRoleVO(roles);
        PageInfo<RoleVO> pageInfo = new PageInfo<>();
        pageInfo.setList(result);
        pageInfo.setTotal(rolePageInfo.getTotal());
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    @Override
    public RoleVO details(Long id) {
        Role role = checkAndResult(id);
        RoleVO roleVO = RoleMapping.INSTANCE.toRoleVO(role);
        // 查询角色对应的权限
        List<RoleAuthority> roleAuthorities = roleAuthorityService.findByRoleId(id);
        if(!CollectionUtils.isEmpty(roleAuthorities)){
            List<Long> authorityIds = roleAuthorities.stream().map(RoleAuthority::getAuthorityId).toList();
            roleVO.setAuthorityIds(authorityIds);
        }
        // 查询角色关联的用户
        List<UserVO> users = roleServiceProxy.findUserByRoleId(id);
        if(!CollectionUtils.isEmpty(users)){
            List<Long> userIds = users.stream().map(UserVO::getId).toList();
            roleVO.setUserIds(userIds);
        }
        return roleVO;
    }

    @Override
    public RoleVO findById(Long roleId) {
        Role role = checkAndResult(roleId);
        return RoleMapping.INSTANCE.toRoleVO(role);
    }

    @Override
    @Cacheable(value = "role:authority", key = "#roleId")
    public List<AuthorityVO> findAuthorityByRoleId(Long roleId) {
        if(roleId == null){
            return Collections.emptyList();
        }
        QueryWrapper<RoleAuthority> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .eq(RoleAuthority::getRoleId, roleId);
        List<RoleAuthority> roleAuthorities = roleAuthorityService.list(roleAuthorityQueryWrapper);
        if(CollectionUtils.isEmpty(roleAuthorities)){
            return Collections.emptyList();
        }
        Set<Long> authorityIds = roleAuthorities.stream().map(RoleAuthority::getAuthorityId).collect(Collectors.toSet());
        return authorityService.findByAuthorityId(authorityIds);
    }

    // 角色绑定权限
    @Override
    @Transactional
    @CachePut(value = "role:authority",key = "#roleId")
    public List<AuthorityVO> bindRoleAuthorities(Long roleId, List<Long> authorityIds) {
        if (roleId == null) {
            log.warn("buildRoleAuthorities called with empty roleId");
            return Collections.emptyList();
        }
        // 先删除原有的角色权限
        roleServiceProxy.unbindRoleAuthorities(roleId);
        // 再添加新的角色权限
        addRoleAuthorities(roleId, authorityIds);
        // 返回角色对应的权限(更新缓存)
        return roleServiceProxy.findAuthorityByRoleId(roleId);
    }

    // 解绑角色下所有权限
    @Override
    @CacheEvict(value = "role:authority", key = "#roleId")
    public Boolean unbindRoleAuthorities(Long roleId) {
        if(roleId == null){
            log.info("unbindRoleAuthorities called with null roleId");
            return true;
        }
        QueryWrapper<RoleAuthority> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .eq(RoleAuthority::getRoleId, roleId);
        // 删除原有的角色权限
        return roleAuthorityService.remove(roleAuthorityQueryWrapper);
    }

    // 解绑权限对应的所有角色
    @Override
    @CacheEvict(value = "role:authority", allEntries = true)
    public Boolean unbindAuthorityRole(Collection<Long> authorityIds) {
        if(CollectionUtils.isEmpty(authorityIds)){
            log.info("unbindAuthorityRole called with empty authorityIds");
            return true;
        }
        QueryWrapper<RoleAuthority> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .in(RoleAuthority::getAuthorityId, authorityIds);
        // 删除角色对应的权限
        return roleAuthorityService.remove(roleAuthorityQueryWrapper);
    }

    @Transactional
    public List<RoleAuthority> addRoleAuthorities(Long roleId, Collection<Long> authorityIds){
        if(CollectionUtils.isEmpty(authorityIds)){
            return Collections.emptyList();
        }
        // 添加新的角色权限
        List<RoleAuthority> roleAuthorities = new ArrayList<>();
        for (Long authorityId : authorityIds) {
            RoleAuthority roleAuthority = new RoleAuthority();
            roleAuthority.setId(IdGen.genId());
            roleAuthority.setRoleId(roleId);
            roleAuthority.setAuthorityId(authorityId);
            roleAuthorities.add(roleAuthority);
        }
        roleAuthorityService.saveBatch(roleAuthorities);
        return roleAuthorities;
    }

    // 查询角色对应的用户
    @Override
    @Cacheable(value = "role:user", key = "#roleId")
    public List<UserVO> findUserByRoleId(Long roleId) {
        if (roleId == null) {
            log.warn("findUserByRoleId called with null roleId");
            return Collections.emptyList();
        }
        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper
                .lambda()
                .eq(UserRole::getRoleId, roleId);
        List<UserRole> userRoles = userRoleService.list(userRoleQueryWrapper);
        Set<Long> userIds = userRoles.stream().map(UserRole::getUserId).collect(Collectors.toSet());
        return userService.findByUserId(userIds);
    }

    // 角色绑定用户
    @Override
    @Transactional
    @CachePut(value = "role:user",key = "#roleId")
    public List<UserVO> bindRoleUsers(Long roleId, List<Long> userIds) {
        // 先删除角色已有的用户关联
        roleServiceProxy.unbindRoleUsers(roleId);
        // 再添加新的用户关联
        addUserRole(userIds, Collections.singletonList(roleId));
        // 返回角色对应的用户(更新缓存)
        return roleServiceProxy.findUserByRoleId(roleId);
    }

    // 解绑角色下所有用户
    @Caching(evict = {
            @CacheEvict(value = "user:role", allEntries = true),
            @CacheEvict(value = "role:user", key = "#roleId")
    })
    public Boolean unbindRoleUsers(Long roleId) {
        if(roleId == null){
            log.info("unbindRoleUsers called with null roleId");
            return true;
        }
        QueryWrapper<UserRole> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .eq(UserRole::getRoleId, roleId);
        // 删除角色对应的用户关联
        return userRoleService.remove(roleAuthorityQueryWrapper);
    }

    // 查询用户对应的角色
    @Override
    @Cacheable(value = "user:role", key = "#userId")
    public List<RoleVO> findRoleByUserId(Long userId) {
        if (userId == null) {
            log.warn("findRoleByUserId called with null userId");
            return Collections.emptyList();
        }
        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper
                .lambda()
                .eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleService.list(userRoleQueryWrapper);
        if(CollectionUtils.isEmpty(userRoles)){
            return Collections.emptyList();
        }
        Set<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        return findRoleByIds(roleIds);
    }

    // 用户绑定角色
    @Override
    @Transactional
    @CachePut(value = "user:role",key = "#userId")
    public List<RoleVO> bindUserRole(Long userId, Collection<Long> roleIds) {
        // 先删除用户已有的角色关联
        roleServiceProxy.unbindUserRoles(userId);
        // 再添加新的角色关联
        addUserRole(Collections.singletonList(userId), roleIds);
        // 返回用户对应的角色(更新缓存)
        return roleServiceProxy.findRoleByUserId(userId);
    }

    // 解绑用户下所有角色
    @Caching(evict = {
            @CacheEvict(value = "user:role", key = "#userId"),
            @CacheEvict(value = "role:user", allEntries = true)
    })
    public Boolean unbindUserRoles(Long userId) {
        if(userId == null){
            log.info("unbindUserRoles called with null userId");
            return true;
        }
        List<RoleVO> roles = roleServiceProxy.findRoleByUserId(userId);
        // 排除超级管理员角色
        List<Long> roleIds = roles.stream().filter(r -> !r.isSuperAdmin()).map(RoleVO::getId).toList();
        if(CollectionUtils.isEmpty(roleIds)){
            return true;
        }
        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper
                .lambda()
                .eq(UserRole::getUserId, userId)
                .in(UserRole::getRoleId, roleIds);
        // 删除用户对应的角色关联
        return userRoleService.remove(userRoleQueryWrapper);
    }

    @Transactional
    public List<UserRole> addUserRole(Collection<Long> userIds, Collection<Long> roleIds){
        if(CollectionUtils.isEmpty(userIds) || CollectionUtils.isEmpty(roleIds)){
            return Collections.emptyList();
        }
        List<UserRole> result = new ArrayList<>();
        for (Long userId : userIds) {
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setId(IdGen.genId());
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                result.add(userRole);
            }
        }
        userRoleService.saveBatch(result);
        return result;
    }

    private List<RoleVO> findRoleByIds(Collection<Long> roleIds){
        if(CollectionUtils.isEmpty(roleIds)){
            return Collections.emptyList();
        }
        QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
        roleQueryWrapper
                .lambda()
                .in(Role::getId, roleIds)
                .eq(Role::getEnabled, true);
        List<Role> roles = roleMapper.selectList(roleQueryWrapper);
        return RoleMapping.INSTANCE.toRoleVO(roles);
    }

    // 删除角色对应的用户关联
    @Override
    public Boolean deleteById(Long roleId) {
        Role role = checkAndResult(roleId);
        if(role.isSuperAdmin()){
            throw new BusinessException("超级管理员角色无法删除");
        }
        int i = roleMapper.deleteById(roleId);
        if(i <= 0){
            throw new BusinessException("删除角色失败，角色可能不存在");
        }
        return true;
    }

    @Override
    public List<RoleVO> listRoleOptions() {
        QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
        roleQueryWrapper
                .lambda()
                .select(Role::getId,Role::getName)
                .eq(Role::getType,RoleType.NORMAL)
                .eq(Role::getEnabled, true);
        List<Role> roles = roleMapper.selectList(roleQueryWrapper);
        return RoleMapping.INSTANCE.toRoleVO(roles);
    }

    private Role checkAndResult(Serializable id){
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }

    private QueryWrapper<Role> getRoleQueryWrapper(RoleQueryDTO queryDTO) {
        QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
        roleQueryWrapper.eq("type",RoleType.NORMAL);
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            roleQueryWrapper
                    .lambda()
                    .like(Role::getName, queryDTO.getKeyword())
                    .or()
                    .like(Role::getCode, queryDTO.getKeyword());
        }
        if (queryDTO.getEnabled() != null) {
            roleQueryWrapper.eq("enabled", queryDTO.getEnabled());
        }
        roleQueryWrapper.orderByDesc("create_time");
        return roleQueryWrapper;
    }
}
