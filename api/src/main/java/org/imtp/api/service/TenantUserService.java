package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.imtp.api.domain.entity.TenantUser;

import java.util.Collection;
import java.util.List;


public interface TenantUserService extends IService<TenantUser> {

    List<Long> findUserIdByTenantId(Long tenantId);

}
