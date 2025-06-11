package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.imtp.api.domain.dto.*;
import org.imtp.api.domain.entity.Role;
import org.imtp.api.domain.vo.RoleVO;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/3 15:36
 */
public interface RoleService extends IService<Role> {

    Long create(RoleCreateDTO roleCreateDTO);

    Integer update(RoleUpdateDTO roleUpdateDTO);

    Boolean bindAuthorities(Long roleId, List<Long> authorityIds);

    Boolean bindUsers(Long id, List<Long> userIds);

    Integer updatePatch(RoleUpdateDTO roleUpdateDTO);

    PageInfo<RoleVO> queryList(RoleQueryDTO queryDTO);

    List<RoleVO> listRoleOptions();

    Integer delete(String id);

    List<RoleVO> findRoleByUserId(Long userId);

}
