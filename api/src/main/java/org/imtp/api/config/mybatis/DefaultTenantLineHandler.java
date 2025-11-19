package org.imtp.api.config.mybatis;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.imtp.api.context.TenantContext;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/11/18 15:41
 */
@Component
@ConfigurationProperties(prefix = "api.tenant")
public class DefaultTenantLineHandler implements TenantLineHandler {


    private List<String> ignoreTables;

    @Override
    public Expression getTenantId() {
        Long tenantId = TenantContext.getTenantId();
        return tenantId == null ? null : new LongValue(tenantId);
    }

    @Override
    public String getTenantIdColumn() {

        return "tenant_id";
    }

    @Override
    public boolean ignoreTable(String tableName) {

        return ignoreTables.contains(tableName);
    }

    public List<String> getIgnoreTables() {
        return ignoreTables;
    }

    public void setIgnoreTables(List<String> ignoreTables) {
        this.ignoreTables = ignoreTables;
    }
}
