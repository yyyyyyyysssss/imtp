package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.domain.dto.*;
import org.imtp.api.domain.entity.Role;
import org.imtp.api.domain.entity.RoleAuthority;
import org.imtp.api.domain.vo.RoleVO;
import org.imtp.api.mapper.RoleMapper;
import org.imtp.api.mapping.RoleMapping;
import org.imtp.api.service.RoleAuthorityService;
import org.imtp.api.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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

    @Override
    @Transactional
    public Long create(RoleCreateDTO roleCreateDTO) {
        Role role = RoleMapping.INSTANCE.toRole(roleCreateDTO);
        role.setId(IdGen.genId());
        int row = roleMapper.insert(role);
        if (row <= 0) {
            throw new BusinessException("创建角色失败");
        }
        if(!CollectionUtils.isEmpty(roleCreateDTO.getAuthorityIds())){
            addRoleAuthority(role.getId(), roleCreateDTO.getAuthorityIds());
        }
        return role.getId();
    }

    @Override
    @Transactional
    public Integer update(RoleUpdateDTO roleUpdateDTO) {
        Role role = roleMapper.selectById(roleUpdateDTO.getId());
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if(role.isSuperAdmin()){
            throw new BusinessException("超级管理员角色无法修改");
        }
        RoleMapping.INSTANCE.overwriteRole(roleUpdateDTO, role);
        int i = roleMapper.updateById(role);
        if (i <= 0) {
            throw new BusinessException("更新角色失败");
        }
        addRoleAuthority(role.getId(), roleUpdateDTO.getAuthorityIds());
        return i;
    }

    @Override
    @Transactional
    public Integer updatePatch(RoleUpdateDTO roleUpdateDTO) {
        Role role = roleMapper.selectById(roleUpdateDTO.getId());
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if(role.isSuperAdmin()){
            throw new BusinessException("超级管理员角色无法修改");
        }
        RoleMapping.INSTANCE.updateRole(roleUpdateDTO, role);
        int i = roleMapper.updateById(role);
        if (i <= 0) {
            throw new BusinessException("更新角色失败");
        }
        if(!CollectionUtils.isEmpty(roleUpdateDTO.getAuthorityIds())){
            addRoleAuthority(role.getId(), roleUpdateDTO.getAuthorityIds());
        }
        return i;
    }

    @Override
    @Transactional
    public Boolean bindAuthorities(Long id, RoleBindAuthoritiesDTO roleBindAuthoritiesDTO) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if(role.isSuperAdmin()){
            throw new BusinessException("超级管理员角色无法修改");
        }
        return addRoleAuthority(role.getId(), roleBindAuthoritiesDTO.getAuthorityIds());
    }

    @Override
    public Boolean bindUsers(Long id, RoleBindUserDTO roleBindUserDTO) {
        return null;
    }

    @Override
    public Integer delete(String id) {
        int i = roleMapper.deleteById(id);
        if (i > 0){
            QueryWrapper<RoleAuthority> roleAuthorityQueryWrapper = new QueryWrapper<>();
            roleAuthorityQueryWrapper
                    .lambda()
                    .eq(RoleAuthority::getRoleId, id);
            // 删除角色对应的权限
            roleAuthorityService.remove(roleAuthorityQueryWrapper);
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
        QueryWrapper<RoleAuthority> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .in(RoleAuthority::getRoleId, roleIds);
        List<RoleAuthority> roleAuthorities = roleAuthorityService.list(roleAuthorityQueryWrapper);
        Map<Long, List<Long>> roleAuthorityIdMap = roleAuthorities.stream().collect(Collectors.groupingBy(
                RoleAuthority::getRoleId,
                Collectors.mapping(RoleAuthority::getAuthorityId, Collectors.toList()
        )));

        List<RoleVO> result = new ArrayList<>();
        for (Role role : roles) {
            RoleVO roleVO = RoleMapping.INSTANCE.toRoleVO(role);
            List<Long> authorityIds = roleAuthorityIdMap.getOrDefault(role.getId(), new ArrayList<>());
            roleVO.setAuthorityIds(authorityIds);
            result.add(roleVO);
        }
        PageInfo<RoleVO> pageInfo = new PageInfo<>();
        pageInfo.setList(result);
        pageInfo.setTotal(rolePageInfo.getTotal());
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    @Transactional
    public boolean addRoleAuthority(Long roleId, Collection<Long> authorityIds) {
        QueryWrapper<RoleAuthority> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .eq(RoleAuthority::getRoleId, roleId);
        // 删除原有的角色权限
        roleAuthorityService.remove(roleAuthorityQueryWrapper);
        if (CollectionUtils.isEmpty(authorityIds)){
            log.warn("添加角色权限时，权限列表为空");
            return false;
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
        return roleAuthorityService.saveBatch(roleAuthorities);
    }

    private QueryWrapper<Role> getRoleQueryWrapper(RoleQueryDTO queryDTO) {
        QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
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
