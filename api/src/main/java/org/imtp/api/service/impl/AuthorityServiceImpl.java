package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.domain.dto.AuthorityAddDTO;
import org.imtp.api.domain.dto.AuthorityUpdateDTO;
import org.imtp.api.domain.entity.Authority;
import org.imtp.api.domain.vo.AuthorityVO;
import org.imtp.api.mapper.AuthorityMapper;
import org.imtp.api.mapping.AuthorityMapping;
import org.imtp.api.service.AuthorityService;
import org.imtp.api.utils.TreeUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/16 23:35
 */
@Service
@Slf4j
public class AuthorityServiceImpl extends ServiceImpl<AuthorityMapper, Authority> implements AuthorityService {

    @Resource
    private AuthorityMapper authorityMapper;

    @Override
    public Long create(AuthorityAddDTO authorityAddDTO) {
        Authority authority = AuthorityMapping.INSTANCE.toAuthority(authorityAddDTO);
        authority.setId(IdGen.genId());
        if(authority.getParentId() != null){
            Authority selectAuthority = authorityMapper.selectById(authority.getParentId());
            authority.setRootId(selectAuthority.getRootId());
        }else {
            authority.setRootId(authority.getId());
        }
        int insert = authorityMapper.insert(authority);
        return insert > 0 ? authority.getId() : null;
    }

    @Override
    public Integer update(AuthorityUpdateDTO authorityUpdateDTO) {
        Authority authority = AuthorityMapping.INSTANCE.toAuthority(authorityUpdateDTO);
        return authorityMapper.updateById(authority);
    }

    @Override
    public AuthorityVO details(String id) {
        Authority authority = authorityMapper.selectById(id);
        return AuthorityMapping.INSTANCE.toAuthorityVO(authority);
    }

    @Override
    public List<AuthorityVO> tree() {
        List<Authority> authorities = this.list();
        if (authorities == null || authorities.isEmpty()){
            return new ArrayList<>();
        }
        List<AuthorityVO> authorityList = AuthorityMapping.INSTANCE.toAuthorityVO(authorities);
        return TreeUtil.buildTree(
                authorityList,
                AuthorityVO::getId,
                AuthorityVO::getParentId,
                AuthorityVO::setChildren,
                null
        );
    }

    @Override
    public Integer delete(String id) {
        List<Authority> authorities = authorityMapper.selectChildrenById(id);
        if (authorities == null || authorities.isEmpty()){
            throw new BusinessException("该权限不存在");
        }
        Set<Long> ids = authorities.stream().map(Authority::getId).collect(Collectors.toSet());
        return authorityMapper.deleteBatchIds(ids);
    }

    @Override
    public Integer batchDelete(Collection<String> ids) {
        return authorityMapper.deleteBatchIds(ids);
    }
}
