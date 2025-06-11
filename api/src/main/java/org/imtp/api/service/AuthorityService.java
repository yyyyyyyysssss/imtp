package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.imtp.api.domain.dto.AuthorityCreateDTO;
import org.imtp.api.domain.dto.AuthorityUpdateDTO;
import org.imtp.api.domain.entity.Authority;
import org.imtp.api.domain.vo.AuthorityVO;

import java.util.Collection;
import java.util.List;

public interface AuthorityService extends IService<Authority> {

    Long create(AuthorityCreateDTO authorityAddDTO);

    Integer update(AuthorityUpdateDTO authorityUpdateDTO);

    Integer updatePatch(AuthorityUpdateDTO authorityUpdateDTO);

    AuthorityVO details(String id);

    List<AuthorityVO> tree();

    Integer delete(String id);

    Integer batchDelete(Collection<String> ids);

    List<AuthorityVO> findAuthorityByRoleIds(Collection<Long> menuIds);
}
