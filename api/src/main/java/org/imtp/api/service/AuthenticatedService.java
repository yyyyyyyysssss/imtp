package org.imtp.api.service;

import jakarta.annotation.Resource;
import org.imtp.api.domain.vo.AuthorityVO;
import org.imtp.api.domain.vo.RoleVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/11 13:06
 */
@Service
public class AuthenticatedService {

    @Resource
    private RoleService roleService;

    @Resource
    private AuthorityService authorityService;

    public List<RoleVO> listRoleOptions() {
        return roleService.listRoleOptions();
    }

    public List<AuthorityVO> treeAuthorityOptions(){

        return authorityService.tree();
    }


}
