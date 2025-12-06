package org.imtp.api.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.imtp.api.context.TenantContext;
import org.imtp.api.domain.entity.User;
import org.imtp.api.utils.SecurityUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/17 11:26
 */
@Component
public class BaseMetaHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        Long userId = SecurityUtils.getCurrentUser(User::getId);
        this.strictInsertFill(metaObject, "creatorId", Long.class, userId);
        this.strictInsertFill(metaObject, "updaterId", Long.class, userId);

        // 自动填充租户 ID 字段
        Long tenantId = TenantContext.getTenantId();
        this.strictInsertFill(metaObject, "tenantId", Long.class, tenantId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        Long userId = SecurityUtils.getCurrentUser(User::getId);
        this.setFieldValByName("updaterId", userId, metaObject);
    }


}
