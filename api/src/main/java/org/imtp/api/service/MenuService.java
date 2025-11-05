package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.imtp.api.domain.dto.MenuCreateDTO;
import org.imtp.api.domain.dto.MenuDragDTO;
import org.imtp.api.domain.dto.MenuQueryDTO;
import org.imtp.api.domain.dto.MenuUpdateDTO;
import org.imtp.api.domain.entity.Authority;
import org.imtp.api.domain.vo.MenuVO;

import java.util.Collection;
import java.util.List;

public interface MenuService extends IService<Authority> {

    Long createMenu(MenuCreateDTO menuCreateDTO);

    Integer updateMenu(MenuUpdateDTO menuUpdateDTO);

    Boolean deleteMenu(Long id);

    Boolean menuDrag(MenuDragDTO menuDragDTO);

    List<MenuVO> tree();

    PageInfo<MenuVO> query(MenuQueryDTO menuQueryDTO);

    MenuVO details(Long id);

    List<MenuVO> findByUserId(Long userId);

    List<MenuVO> findByUserId(Long userId, Collection<Long> roleIds);
}
