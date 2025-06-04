package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.imtp.api.domain.dto.RoleCreateDTO;
import org.imtp.api.domain.dto.RoleQueryDTO;
import org.imtp.api.domain.dto.RoleUpdateDTO;
import org.imtp.api.domain.entity.Role;
import org.imtp.api.domain.vo.RoleVO;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/3 15:36
 */
public interface RoleService extends IService<Role> {

    Long create(RoleCreateDTO roleCreateDTO);

    Integer update(RoleUpdateDTO roleUpdateDTO);

    Integer updatePatch(RoleUpdateDTO roleUpdateDTO);

    PageInfo<RoleVO> queryList(RoleQueryDTO queryDTO);

    Integer delete(String id);

}
