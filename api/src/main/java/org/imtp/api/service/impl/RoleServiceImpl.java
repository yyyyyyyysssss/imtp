package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.domain.dto.RoleCreateDTO;
import org.imtp.api.domain.dto.RoleQueryDTO;
import org.imtp.api.domain.dto.RoleUpdateDTO;
import org.imtp.api.domain.entity.Role;
import org.imtp.api.domain.entity.RoleAuthority;
import org.imtp.api.domain.vo.RoleVO;
import org.imtp.api.mapper.RoleAuthorityMapper;
import org.imtp.api.mapper.RoleMapper;
import org.imtp.api.mapping.RoleMapping;
import org.imtp.api.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private RoleAuthorityMapper roleAuthorityMapper;

    @Override
    public Long create(RoleCreateDTO roleCreateDTO) {
        Role role = RoleMapping.INSTANCE.toRole(roleCreateDTO);
        role.setId(IdGen.genId());
        int row = roleMapper.insert(role);
        if (row <= 0) {
            throw new BusinessException("创建角色失败");
        }
        return role.getId();
    }

    @Override
    public Integer update(RoleUpdateDTO roleUpdateDTO) {
        Role role = roleMapper.selectById(roleUpdateDTO.getId());
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        RoleMapping.INSTANCE.overwriteRole(roleUpdateDTO, role);
        return roleMapper.updateById(role);
    }

    @Override
    public Integer updatePatch(RoleUpdateDTO roleUpdateDTO) {
        Role role = roleMapper.selectById(roleUpdateDTO.getId());
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        RoleMapping.INSTANCE.updateRole(roleUpdateDTO, role);
        return roleMapper.updateById(role);
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
            roleAuthorityMapper.delete(roleAuthorityQueryWrapper);
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
        PageInfo<Role> rolePageInfo = PageInfo.of(roles);

        List<RoleVO> roleVOs = roles.stream()
                .map(RoleMapping.INSTANCE::toRoleVO)
                .toList();
        PageInfo<RoleVO> pageInfo = new PageInfo<>();
        pageInfo.setList(roleVOs);
        pageInfo.setTotal(rolePageInfo.getTotal());
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }



    private QueryWrapper<Role> getRoleQueryWrapper(RoleQueryDTO queryDTO) {
        QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
        if (queryDTO.getName() != null && !queryDTO.getName().isEmpty()) {
            roleQueryWrapper.like("name", queryDTO.getName());
        }
        if (queryDTO.getCode() != null && !queryDTO.getCode().isEmpty()) {
            roleQueryWrapper.like("code", queryDTO.getCode());
        }
        if (queryDTO.getEnabled() != null) {
            roleQueryWrapper.eq("enabled", queryDTO.getEnabled());
        }
        roleQueryWrapper.orderByDesc("create_time");
        return roleQueryWrapper;
    }
}
