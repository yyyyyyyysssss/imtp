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
import org.imtp.api.enums.AuthorityType;
import org.imtp.api.mapper.AuthorityMapper;
import org.imtp.api.mapping.AuthorityMapping;
import org.imtp.api.service.AuthorityService;
import org.imtp.api.utils.TreeUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
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

    @Override
    public Long create(AuthorityCreateDTO authorityAddDTO) {
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
    public Integer update(AuthorityUpdateDTO authorityUpdateDTO) {
        Authority authority = authorityMapper.selectById(authorityUpdateDTO.getId());
        if (authority == null || !authority.getType().equals(AuthorityType.BUTTON)) {
            throw new BusinessException("该操作权限不存在");
        }
        AuthorityMapping.INSTANCE.overwriteAuthority(authorityUpdateDTO,authority);
        if(authorityUpdateDTO.getParentId() != null && !authorityUpdateDTO.getParentId().isEmpty() && !authorityUpdateDTO.getParentId().equals(authority.getParentId().toString())){
            Authority selectAuthority = authorityMapper.selectById(authorityUpdateDTO.getParentId());
            authority.setRootId(selectAuthority.getRootId());
        }
        return authorityMapper.updateById(authority);
    }

    @Override
    public Integer updatePatch(AuthorityUpdateDTO authorityUpdateDTO) {
        Authority authority = authorityMapper.selectById(authorityUpdateDTO.getId());
        if (authority == null || !authority.getType().equals(AuthorityType.BUTTON)) {
            throw new BusinessException("该操作权限不存在");
        }
        AuthorityMapping.INSTANCE.updateAuthority(authorityUpdateDTO,authority);
        if(authorityUpdateDTO.getParentId() != null && !authorityUpdateDTO.getParentId().isEmpty() && !authorityUpdateDTO.getParentId().equals(authority.getParentId().toString())){
            Authority selectAuthority = authorityMapper.selectById(authorityUpdateDTO.getParentId());
            authority.setRootId(selectAuthority.getRootId());
        }
        return authorityMapper.updateById(authority);
    }

    @Override
    public AuthorityVO details(String id) {

        return null;
    }

    @Override
    public List<AuthorityVO> tree() {
        QueryWrapper<Authority> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("type", AuthorityType.MENU.name(),AuthorityType.BUTTON.name());
        List<Authority> authorities = authorityMapper.selectList(queryWrapper);
        if (authorities == null || authorities.isEmpty()){
            return new ArrayList<>();
        }
        List<AuthorityVO> authorityList = AuthorityMapping.INSTANCE.toAuthorityVO(authorities);
        return TreeUtil.buildTree(
                authorityList,
                AuthorityVO::getId,
                AuthorityVO::getParentId,
                AuthorityVO::setChildren,
                0L
        );
    }

    @Override
    public Integer delete(String id) {
        Authority authority = authorityMapper.selectById(id);
        if (authority == null || !authority.getType().equals(AuthorityType.BUTTON)){
            throw new BusinessException("该权限不存在");
        }
        return authorityMapper.deleteById(id);
    }

    @Override
    public Integer batchDelete(Collection<String> ids) {
        List<Authority> authorities = authorityMapper.selectBatchIds(ids);
        if (authorities == null || authorities.isEmpty()){
            throw new BusinessException("权限不存在");
        }
        if (authorities.stream().anyMatch(f -> !f.getType().equals(AuthorityType.BUTTON))){
            throw new BusinessException("存在非权限类型的权限");
        }
        return authorityMapper.deleteBatchIds(ids);
    }
}
