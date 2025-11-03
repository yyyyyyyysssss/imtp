package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.imtp.api.domain.dto.AuthorityCreateDTO;
import org.imtp.api.domain.dto.AuthorityUpdateDTO;
import org.imtp.api.domain.entity.Authority;
import org.imtp.api.domain.vo.AuthorityVO;

import java.util.Collection;
import java.util.List;

public interface AuthorityService extends IService<Authority> {

    Long createAuthority(AuthorityCreateDTO authorityAddDTO);

    Boolean updateAuthority(AuthorityUpdateDTO authorityUpdateDTO, Boolean isFullUpdate);

    AuthorityVO details(String id);

    List<AuthorityVO> tree();

    Boolean deleteAuthority(Long id);

    List<AuthorityVO> findByRoleId(Long roleId);

    List<AuthorityVO> findByUserId(Long userId);
}
