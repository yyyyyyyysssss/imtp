package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.imtp.api.domain.dto.MenuCreateDTO;
import org.imtp.api.domain.dto.MenuUpdateDTO;
import org.imtp.api.domain.entity.Authority;
import org.imtp.api.domain.vo.AuthorityVO;
import org.imtp.api.domain.vo.MenuVO;

import java.util.Collection;
import java.util.List;

public interface MenuService extends IService<Authority> {

    Long create(MenuCreateDTO menuCreateDTO);

    Integer update(MenuUpdateDTO menuUpdateDTO);

    MenuVO details(String id);

    List<MenuVO> getMenuByUserId(Long userId);

    Integer delete(String id);

    Integer batchDelete(Collection<String> ids);

}
