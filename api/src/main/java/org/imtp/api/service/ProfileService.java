package org.imtp.api.service;

import org.imtp.api.domain.dto.ChangePasswordDTO;
import org.imtp.api.domain.vo.TenantVO;
import org.imtp.api.domain.vo.UserInfoVO;

import java.util.List;

public interface ProfileService {

    UserInfoVO userInfo(Long userId);

    List<TenantVO> findUserTenant(Long userId);

    void switchTenant(Long userId, Long tenantId, String token);

    Boolean changePassword(Long userId, ChangePasswordDTO changePasswordDTO);

    Boolean changeAvatar(Long userId, String avatarUrl);

}
