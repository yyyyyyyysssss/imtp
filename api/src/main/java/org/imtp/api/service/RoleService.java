package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.imtp.api.domain.dto.RoleCreateDTO;
import org.imtp.api.domain.dto.RoleQueryDTO;
import org.imtp.api.domain.dto.RoleUpdateDTO;
import org.imtp.api.domain.entity.Role;
import org.imtp.api.domain.entity.UserRole;
import org.imtp.api.domain.vo.AuthorityVO;
import org.imtp.api.domain.vo.RoleVO;
import org.imtp.api.domain.vo.UserVO;

import java.util.Collection;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/3 15:36
 */
public interface RoleService extends IService<Role> {

    Long create(RoleCreateDTO roleCreateDTO);

    Integer update(RoleUpdateDTO roleUpdateDTO);

    Integer updatePartial(RoleUpdateDTO roleUpdateDTO);

    List<AuthorityVO> findAuthorityByRoleId(Long roleId);

    List<AuthorityVO> bindRoleAuthorities(Long roleId, List<Long> authorityIds);

    Boolean unbindRoleAuthorities(Long roleId);

    Boolean unbindAuthorityRole(Collection<Long> authorityIds);

    List<UserVO> findUserByRoleId(Long roleId);

    List<UserVO> bindRoleUsers(Long roleId, List<Long> userIds);

    Boolean unbindRoleUsers(Long roleId);

    List<RoleVO> findRoleByUserId(Long userId);

    List<RoleVO> bindUserRole(Long userId, Collection<Long> roleIds);

    Boolean unbindUserRoles(Long userId);

    RoleVO findById(Long roleId);

    PageInfo<RoleVO> queryList(RoleQueryDTO queryDTO);

    RoleVO details(Long id);

    List<RoleVO> listRoleOptions();

    Boolean deleteById(Long roleId);

}
