package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.domain.dto.AuthorityCreateDTO;
import org.imtp.api.domain.dto.AuthorityUpdateDTO;
import org.imtp.api.domain.entity.Authority;
import org.imtp.api.domain.vo.AuthorityVO;
import org.imtp.api.domain.vo.RoleVO;
import org.imtp.api.enums.AuthorityType;
import org.imtp.api.mapper.AuthorityMapper;
import org.imtp.api.mapping.AuthorityMapping;
import org.imtp.api.service.AuthorityService;
import org.imtp.api.service.RoleAuthorityService;
import org.imtp.api.service.RoleService;
import org.imtp.api.utils.TreeUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/16 23:35
 */
@Service
@Slf4j
public class AuthorityServiceImpl extends AbstractAuthorityService implements AuthorityService {

    @Resource
    private AuthorityMapper authorityMapper;

    @Resource
    private RoleService roleService;

    @Resource
    private RoleAuthorityService roleAuthorityService;

    @Override
    public Long createAuthority(AuthorityCreateDTO authorityAddDTO) {
        Authority authority = AuthorityMapping.INSTANCE.toAuthority(authorityAddDTO);
        authority.setId(IdGen.genId());
        authority.setType(AuthorityType.BUTTON);
        Authority selectAuthority = authorityMapper.selectById(authority.getParentId());
        authority.setRootId(selectAuthority.getRootId());
        if (authority.getSort() == null){
            Long parentId = authority.getParentId();
            if (authority.getParentId() == null){
                parentId = 0L;
            }
            int maxSortOfChildren = getMaxSortOfChildren(parentId);
            authority.setSort(maxSortOfChildren + 1);
        }
        int insert = authorityMapper.insert(authority);
        return insert > 0 ? authority.getId() : null;
    }

    @Override
    public Boolean updateAuthority(AuthorityUpdateDTO authorityUpdateDTO, Boolean isFullUpdate) {
        Authority authority = authorityMapper.selectById(authorityUpdateDTO.getId());
        if (authority == null || !authority.getType().equals(AuthorityType.BUTTON)) {
            throw new BusinessException("该操作权限不存在");
        }
        if (isFullUpdate){
            AuthorityMapping.INSTANCE.overwriteAuthority(authorityUpdateDTO,authority);
        } else {
            AuthorityMapping.INSTANCE.updateAuthority(authorityUpdateDTO,authority);
        }
        if(authorityUpdateDTO.getParentId() != null && !authorityUpdateDTO.getParentId().isEmpty() && !authorityUpdateDTO.getParentId().equals(authority.getParentId().toString())){
            Authority selectAuthority = authorityMapper.selectById(authorityUpdateDTO.getParentId());
            authority.setRootId(selectAuthority.getRootId());
        }
        return authorityMapper.updateById(authority) > 0;
    }

    @Override
    public AuthorityVO details(String id) {
        Authority authority = authorityMapper.selectById(id);
        return AuthorityMapping.INSTANCE.toAuthorityVO(authority);
    }

    @Override
    public List<AuthorityVO> tree() {
        QueryWrapper<Authority> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .lambda()
                .select(Authority::getId,Authority::getParentId,Authority::getName)
                .in(Authority::getType, AuthorityType.MENU, AuthorityType.BUTTON)
                .orderByAsc(Authority::getSort,Authority::getId);
        List<Authority> authorities = authorityMapper.selectList(queryWrapper);
        if (authorities == null || authorities.isEmpty()){
            return new ArrayList<>();
        }
        List<AuthorityVO> authorityList = AuthorityMapping.INSTANCE.toAuthorityVO(authorities);
        return TreeUtils.buildTree(
                authorityList,
                AuthorityVO::getId,
                AuthorityVO::getParentId,
                AuthorityVO::setChildren,
                0L
        );
    }

    @Override
    @Transactional
    public Boolean deleteAuthority(Long id) {
        Authority authority = authorityMapper.selectById(id);
        if (authority == null || !authority.getType().equals(AuthorityType.BUTTON)){
            throw new BusinessException("该权限不存在");
        }
        int i = authorityMapper.deleteById(id);
        if(i <= 0){
            throw new BusinessException("删除权限失败");
        }
        // 解绑该权限与角色的关联关系
        roleService.unbindAuthorityRole(id);
        return true;
    }

    // 根据角色ID查询权限
    @Override
    @Cacheable(value = "role:authority", key = "#roleId")
    public List<AuthorityVO> findByRoleId(Long roleId) {
        if(roleId == null){
            return Collections.emptyList();
        }
        return this.findByRoleId(Collections.singletonList(roleId));
    }

    // 根据用户ID查询权限
    @Override
    @Cacheable(value = "user:authority", key = "#userId")
    public List<AuthorityVO> findByUserId(Long userId) {
        if(userId == null){
            return Collections.emptyList();
        }
        List<RoleVO> roles = roleService.findByUserId(userId);
        if(CollectionUtils.isEmpty(roles)){
            return Collections.emptyList();
        }
        List<Long> roleIds = roles.stream().map(RoleVO::getId).toList();
        return this.findByRoleId(roleIds);
    }


    private List<AuthorityVO> findByRoleId(Collection<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        List<Long> authorityIds = roleAuthorityService.findAuthorityIdByRoleId(roleIds);
        if (CollectionUtils.isEmpty(authorityIds)){
            return Collections.emptyList();
        }
        List<Authority> authorities = authorityMapper.selectBatchIds(authorityIds);
        if (CollectionUtils.isEmpty(authorities)) {
            return Collections.emptyList();
        }
        return AuthorityMapping.INSTANCE.toAuthorityVO(authorities);
    }
}
