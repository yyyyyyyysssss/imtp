package org.imtp.api.service;

import org.imtp.api.domain.dto.ChangePasswordDTO;
import org.imtp.api.domain.vo.UserInfoVO;

public interface ProfileService {

    UserInfoVO userInfo(Long userId);

    Boolean changePassword(Long userId, ChangePasswordDTO changePasswordDTO);

}
