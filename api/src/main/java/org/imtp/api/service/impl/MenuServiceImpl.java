package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.domain.dto.MenuCreateDTO;
import org.imtp.api.domain.dto.MenuUpdateDTO;
import org.imtp.api.domain.entity.Authority;
import org.imtp.api.domain.entity.Role;
import org.imtp.api.domain.vo.AuthorityVO;
import org.imtp.api.domain.vo.MenuVO;
import org.imtp.api.enums.AuthorityType;
import org.imtp.api.mapper.AuthorityMapper;
import org.imtp.api.mapper.RoleMapper;
import org.imtp.api.mapping.AuthorityMapping;
import org.imtp.api.service.MenuService;
import org.imtp.api.utils.TreeUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/19 10:26
 */
@Service
@Slf4j
public class MenuServiceImpl extends ServiceImpl<AuthorityMapper, Authority> implements MenuService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private AuthorityMapper authorityMapper;

    @Override
    public Long create(MenuCreateDTO menuCreateDTO) {
        Authority authority = AuthorityMapping.INSTANCE.toAuthority(menuCreateDTO);
        authority.setId(IdGen.genId());
        authority.setType(AuthorityType.MENU);
        if(authority.getParentId() != null){
            Authority selectAuthority = authorityMapper.selectById(authority.getParentId());
            authority.setRootId(selectAuthority.getRootId());
        }else {
            authority.setParentId(0L);
            authority.setRootId(authority.getId());
        }
        int insert = authorityMapper.insert(authority);
        return insert > 0 ? authority.getId() : null;
    }

    @Override
    public Integer update(MenuUpdateDTO menuUpdateDTO) {
        Authority authority = authorityMapper.selectById(menuUpdateDTO.getId());
        if (authority == null || !authority.getType().equals(AuthorityType.MENU)) {
            throw new BusinessException("该菜单不存在");
        }
        AuthorityMapping.INSTANCE.updateAuthority(menuUpdateDTO, authority);
        return authorityMapper.updateById(authority);
    }

    @Override
    public MenuVO details(String id) {
        AuthorityVO authorityVO = authorityMapper.findDetailsById(id);
        return AuthorityMapping.INSTANCE.toMenuVo(authorityVO);
    }

    @Override
    public List<MenuVO> getMenuByUserId(Long userId) {
        List<Role> roles = roleMapper.findRoleByUserIds(Collections.singleton(userId));
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        boolean isSuperAdmin = roles.stream().anyMatch(f -> f.getCode().equals("super_admin"));
        List<Authority> authorities;
        if (isSuperAdmin){
            QueryWrapper<Authority> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("type", AuthorityType.MENU.name());
            authorities = authorityMapper.selectList(queryWrapper);
        }else {
            Set<Long> roleIds = roles.stream().map(Role::getId).collect(Collectors.toSet());
            authorities = authorityMapper.findMenuByRoleIds(roleIds);
        }
        if (CollectionUtils.isEmpty(authorities)) {
            return Collections.emptyList();
        }
        List<MenuVO> menuVOList = authorities.stream().map(AuthorityMapping.INSTANCE::toMenuVo).toList();
        return TreeUtil.buildTree(
                menuVOList,
                MenuVO::getId,
                MenuVO::getParentId,
                MenuVO::setChildren,
                0L
        );
    }

    @Override
    public Integer delete(String id) {
        //查询出菜单对应的所有子菜单或权限
        List<Authority> authorities = authorityMapper.selectChildrenById(id);
        if (authorities == null || authorities.isEmpty()){
            throw new BusinessException("该菜单不存在");
        }
        Set<Long> delIds = authorities.stream().map(Authority::getId).collect(Collectors.toSet());
        return authorityMapper.deleteBatchIds(delIds);
    }

    @Override
    public Integer batchDelete(Collection<String> ids) {
        //批量查询出菜单对应的所有子菜单或权限
        List<Authority> authorities = authorityMapper.selectChildrenByIds(ids);
        if (authorities == null || authorities.isEmpty()){
            throw new BusinessException("菜单不存在");
        }
        Set<Long> delIds = authorities.stream().map(Authority::getId).collect(Collectors.toSet());
        return authorityMapper.deleteBatchIds(delIds);
    }

}
