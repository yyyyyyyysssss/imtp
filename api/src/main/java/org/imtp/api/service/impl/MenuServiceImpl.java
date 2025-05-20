package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.domain.dto.MenuCreateDTO;
import org.imtp.api.domain.dto.MenuQueryDTO;
import org.imtp.api.domain.dto.MenuUpdateDTO;
import org.imtp.api.domain.entity.Authority;
import org.imtp.api.domain.entity.Role;
import org.imtp.api.domain.vo.MenuVO;
import org.imtp.api.enums.AuthorityType;
import org.imtp.api.mapper.AuthorityMapper;
import org.imtp.api.mapper.RoleMapper;
import org.imtp.api.mapping.AuthorityMapping;
import org.imtp.api.service.MenuService;
import org.imtp.api.utils.TreeUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public List<MenuVO> tree() {
        QueryWrapper<Authority> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .lambda()
                .eq(Authority::getType, AuthorityType.MENU.name())
                .orderByAsc(Authority::getId);
        List<Authority> authorities = authorityMapper.selectList(queryWrapper);
        if (authorities == null || authorities.isEmpty()){
            return new ArrayList<>();
        }
        List<MenuVO> menuVOList = AuthorityMapping.INSTANCE.toMenuVo(authorities);
        return TreeUtil.buildTree(
                menuVOList,
                MenuVO::getId,
                MenuVO::getParentId,
                MenuVO::setChildren,
                0L
        );
    }

    @Override
    public PageInfo<MenuVO> query(MenuQueryDTO menuQueryDTO) {
        if (menuQueryDTO.isPaging()){
            PageHelper.startPage(menuQueryDTO.getPageNum(), menuQueryDTO.getPageSize());
        }
        QueryWrapper<Authority> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .lambda()
                .eq(Authority::getType, AuthorityType.MENU.name())
                .eq(Authority::getParentId,0L)
                .eq(StringUtils.isNotEmpty(menuQueryDTO.getCode()),Authority::getCode, menuQueryDTO.getCode())
                .eq(StringUtils.isNotEmpty(menuQueryDTO.getRoutePath()), Authority::getRoutePath, menuQueryDTO.getRoutePath())
                .like(StringUtils.isNotEmpty(menuQueryDTO.getName()), Authority::getName, menuQueryDTO.getName())
                .orderByDesc(Authority::getId);
        List<Authority> authorities = authorityMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(authorities)) {
            return PageInfo.of(new ArrayList<>());
        }
        PageInfo<Authority> authorityPageInfo = PageInfo.of(authorities);
        List<Long> ids = authorities.stream().map(Authority::getId).toList();
        List<Authority> children = authorityMapper.selectChildrenByIds(ids);
        if (!CollectionUtils.isEmpty(children)) {
            authorities = Stream.concat(authorities.stream(), children.stream().filter(f -> f.getType().equals(AuthorityType.MENU))).distinct().toList();
        }
        // 构建树形结构
        List<MenuVO> menuVOList = TreeUtil.buildTree(
                AuthorityMapping.INSTANCE.toMenuVo(authorities),
                MenuVO::getId,
                MenuVO::getParentId,
                MenuVO::setChildren,
                0L
        );
        PageInfo<MenuVO> menuVOPageInfo = new PageInfo<>();
        menuVOPageInfo.setList(menuVOList);
        menuVOPageInfo.setPageNum(menuQueryDTO.getPageNum());
        menuVOPageInfo.setPageSize(menuQueryDTO.getPageSize());
        menuVOPageInfo.setTotal(authorityPageInfo.getTotal());
        return menuVOPageInfo;
    }

    @Override
    public MenuVO details(String id) {
        Authority authority = authorityMapper.selectById(id);
        if (authority == null || !authority.getType().equals(AuthorityType.MENU)) {
            throw new BusinessException("该菜单不存在");
        }
        MenuVO menuVo = AuthorityMapping.INSTANCE.toMenuVo(authority);
        // 找出菜单下关联的权限
        QueryWrapper<Authority> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .lambda()
                .eq(Authority::getType, AuthorityType.PERMISSION.name())
                .eq(Authority::getParentId, id);
        List<Authority> permissions = authorityMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(permissions)) {
            menuVo.setChildren(AuthorityMapping.INSTANCE.toMenuVo(permissions));
        }
        return menuVo;
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
