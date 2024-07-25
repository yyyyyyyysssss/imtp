package org.imtp.web.mapper;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.imtp.web.utils.ReflectUtil;

/**
 * @Description
 * @Author ys
 * @Date 2023/8/1 16:29
 */
public class SelectParentBatchByIds extends AbstractMethod {

    private final static String METHOD_NAME = "selectParentBatchByIds";

    private final static String METHOD_SQL =
            "<script>" +
                "WITH RECURSIVE tmp as (" +
                " SELECT t.* FROM %s t WHERE t.%s IN (%s)\n" +
                " UNION DISTINCT \n" +
                " SELECT f.* FROM %s f INNER JOIN tmp on f.%s = tmp.%s\n" +
                ") select * from tmp %s" +
            "</script>";

    public SelectParentBatchByIds() {
        this(METHOD_NAME);
    }

    public SelectParentBatchByIds(String name) {
        super(name);
    }

    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        //只有具有树关系的实体类类才能使用
        if (!TreeRelation.class.isAssignableFrom(modelClass)){
            return null;
        }
        Object parentFieldName = ReflectUtil.invokeMethodByName(modelClass, "parentFieldName", null);
        Object childFieldName = ReflectUtil.invokeMethodByName(modelClass, "childFieldName", null);
        String sql = String.format(METHOD_SQL,
                tableInfo.getTableName(),
                tableInfo.getKeyColumn(),
                SqlScriptUtils.convertForeach("#{item}", "coll", (String)null, "item", ","),
                tableInfo.getTableName(),
                childFieldName,
                parentFieldName,
                tableInfo.getLogicDeleteSql(true, true));
        SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration,sql,modelClass);
        return this.addSelectMappedStatementForTable(mapperClass, METHOD_NAME, sqlSource, tableInfo);
    }

}
