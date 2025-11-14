package org.imtp.api.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.imtp.api.domain.entity.User;
import org.imtp.api.utils.SecurityUtils;
import org.springframework.stereotype.Component;

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
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());

        Long userId = SecurityUtils.getCurrentUser(User::getId);
        this.strictInsertFill(metaObject, "createBy", Long.class, userId);
        this.strictInsertFill(metaObject, "updateBy", Long.class, userId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
        Long userId = SecurityUtils.getCurrentUser(User::getId);
        this.setFieldValByName("updateBy", userId, metaObject);
    }


}
