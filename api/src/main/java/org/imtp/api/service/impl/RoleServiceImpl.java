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
import org.imtp.api.domain.vo.RoleVO;
import org.imtp.api.enums.RoleType;
import org.imtp.api.mapper.RoleMapper;
import org.imtp.api.mapping.RoleMapping;
import org.imtp.api.service.RoleAuthorityService;
import org.imtp.api.service.RoleService;
import org.imtp.api.service.UserRoleService;
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
@Service
@Slf4j
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>  implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleAuthorityService roleAuthorityService;

    @Resource
    private UserRoleService userRoleService;

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
            bindUsers(role.getId(), roleCreateDTO.getUserIds());
        }
        if(!CollectionUtils.isEmpty(roleCreateDTO.getAuthorityIds())){
            bindAuthorities(role.getId(), roleCreateDTO.getAuthorityIds());
        }
        return role.getId();
    }

    @Override
    @Transactional
    public Integer update(RoleUpdateDTO roleUpdateDTO) {
        Role role = checkAndResult(roleUpdateDTO.getId());
        RoleMapping.INSTANCE.overwriteRole(roleUpdateDTO, role);
        int i = roleMapper.updateById(role);
        if (i <= 0) {
            throw new BusinessException("更新角色失败");
        }
        // 更新角色关联的用户
        bindUsers(role.getId(),roleUpdateDTO.getUserIds());
        // 更新角色关联的权限
        bindAuthorities(role.getId(), roleUpdateDTO.getAuthorityIds());
        return i;
    }

    @Override
    @Transactional
    public Integer updatePartial(RoleUpdateDTO roleUpdateDTO) {
        Role role = checkAndResult(roleUpdateDTO.getId());
        RoleMapping.INSTANCE.updateRole(roleUpdateDTO, role);
        int i = roleMapper.updateById(role);
        if (i <= 0) {
            throw new BusinessException("更新角色失败");
        }
        if(!CollectionUtils.isEmpty(roleUpdateDTO.getUserIds())){
            bindUsers(role.getId(),roleUpdateDTO.getUserIds());
        }
        if(!CollectionUtils.isEmpty(roleUpdateDTO.getAuthorityIds())){
            bindAuthorities(role.getId(), roleUpdateDTO.getAuthorityIds());
        }
        return i;
    }

    @Override
    public Boolean bindAuthorities(Long id, List<Long> authorityIds) {

        return roleAuthorityService.bindRoleAuthorities(Collections.singletonList(id), authorityIds);
    }

    @Override
    public Boolean bindUsers(Long id, List<Long> userIds) {

        return userRoleService.bindUserRoles(userIds, Collections.singletonList(id));
    }

    @Override
    public Integer delete(Long id) {
        checkAndResult(id);
        int i = roleMapper.deleteById(id);
        if (i > 0){
            // 删除角色对应的权限
            roleAuthorityService.deleteByRoleIds(Collections.singletonList(id));
        }else {
            throw new BusinessException("删除角色失败，角色可能不存在");
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

        List<Long> roleIds = roles.stream().map(Role::getId).toList();
        // 查询角色对应的权限
        List<RoleAuthority> roleAuthorities = roleAuthorityService.findByRoleIds(roleIds);
        Map<Long, List<Long>> roleAuthorityIdMap = roleAuthorities.stream().collect(Collectors.groupingBy(
                RoleAuthority::getRoleId,
                Collectors.mapping(RoleAuthority::getAuthorityId, Collectors.toList()
        )));
        // 查询角色关联的用户
        List<UserRole> userRoles = userRoleService.findByRoleIds(roleIds);
        Map<Long, List<Long>> userRoleIdMap = userRoles.stream().collect(Collectors.groupingBy(
                UserRole::getRoleId,
                Collectors.mapping(UserRole::getUserId, Collectors.toList()
                )));


        List<RoleVO> result = new ArrayList<>();
        for (Role role : roles) {
            RoleVO roleVO = RoleMapping.INSTANCE.toRoleVO(role);
            List<Long> authorityIds = roleAuthorityIdMap.getOrDefault(role.getId(), new ArrayList<>());
            List<Long> userIds = userRoleIdMap.getOrDefault(role.getId(), new ArrayList<>());
            roleVO.setAuthorityIds(authorityIds);
            roleVO.setUserIds(userIds);
            result.add(roleVO);
        }
        PageInfo<RoleVO> pageInfo = new PageInfo<>();
        pageInfo.setList(result);
        pageInfo.setTotal(rolePageInfo.getTotal());
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
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


    @Override
    public List<RoleVO> findRoleByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        List<UserRole> userRoles = userRoleService.findByUserIds(Collections.singleton(userId));
        if(CollectionUtils.isEmpty(userRoles)){
            return Collections.emptyList();
        }
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).toList();
        QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
        roleQueryWrapper
                .lambda()
                .in(Role::getId, roleIds)
                .eq(Role::getEnabled, true);
        List<Role> roles = roleMapper.selectList(roleQueryWrapper);
        return RoleMapping.INSTANCE.toRoleVO(roles);
    }

    private Role checkAndResult(Serializable id){
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if(role.isSuperAdmin()){
            throw new BusinessException("超级管理员角色无法操作");
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
