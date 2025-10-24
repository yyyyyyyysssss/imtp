package org.imtp.api.mapper;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.imtp.api.utils.ReflectUtils;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/17 19:48
 */
public class SelectLineageById extends AbstractMethod {
    private final static String METHOD_NAME = "selectLineageById";

    private final static String METHOD_SQL =
            "<script>" +
                    "WITH RECURSIVE tmp_child as (" +
                    " SELECT t.* FROM %s t WHERE t.%s = #{%s} \n" +
                    " UNION ALL \n" +
                    " SELECT f.* FROM %s f INNER JOIN tmp_child tmp on f.%s = tmp.%s \n" +
                    "), " +
                    "tmp_parent as (" +
                    " SELECT t.* FROM %s t WHERE t.%s = #{%s} \n" +
                    " UNION ALL \n" +
                    " SELECT f.* FROM %s f INNER JOIN tmp_parent tmp on f.%s = tmp.%s \n" +
                    ") " +
                    " SELECT * from tmp_child \n" +
                    " UNION \n" +
                    " SELECT * from tmp_parent %s \n" +
                    "</script>";

    public SelectLineageById() {
        this(METHOD_NAME);
    }

    public SelectLineageById(String name) {
        super(name);
    }

    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        //只有具有树关系的实体类类才能使用
        if (!TreeRelation.class.isAssignableFrom(modelClass)){
            return null;
        }
        Object parentFieldName = ReflectUtils.invokeMethodByName(modelClass, "parentFieldName", null);
        Object childFieldName = ReflectUtils.invokeMethodByName(modelClass, "childFieldName", null);
        String sql = String.format(METHOD_SQL,
                tableInfo.getTableName(),
                tableInfo.getKeyColumn(),
                tableInfo.getKeyProperty(),
                tableInfo.getTableName(),
                parentFieldName,
                childFieldName,
                tableInfo.getTableName(),
                tableInfo.getKeyColumn(),
                tableInfo.getKeyProperty(),
                tableInfo.getTableName(),
                childFieldName,
                parentFieldName,
                tableInfo.getLogicDeleteSql(true, true));
        SqlSource sqlSource = super.createSqlSource(this.configuration,sql,Object.class);
        return this.addSelectMappedStatementForTable(mapperClass, METHOD_NAME, sqlSource, tableInfo);
    }
}
