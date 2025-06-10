package org.imtp.api.service;

import org.imtp.api.domain.dto.ChangePasswordDTO;
import org.imtp.api.domain.vo.MenuVO;

import java.util.List;

public interface ProfileService {

    List<MenuVO> getMenuByUserId(Long userId);

    Boolean changePassword(Long userId, ChangePasswordDTO changePasswordDTO);

}
