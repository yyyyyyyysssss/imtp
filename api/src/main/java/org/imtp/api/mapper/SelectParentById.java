package org.imtp.api.mapper;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.imtp.api.utils.ReflectUtils;

/**
 * @Description
 * @Author ys
 * @Date 2023/8/1 16:29
 */
public class SelectParentById extends AbstractMethod {

    private final static String METHOD_NAME = "selectParentById";

    private final static String METHOD_SQL =
            "<script>" +
                "WITH RECURSIVE tmp as (" +
                " SELECT t.* FROM %s t WHERE t.%s = #{%s} \n" +
                " UNION ALL \n" +
                " SELECT f.* FROM %s f INNER JOIN tmp on f.%s = tmp.%s\n" +
                ") select * from tmp %s" +
            "</script>";

    public SelectParentById() {
        this(METHOD_NAME);
    }

    public SelectParentById(String name) {
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
                childFieldName,
                parentFieldName,
                tableInfo.getLogicDeleteSql(true, true));
        SqlSource sqlSource = super.createSqlSource(this.configuration,sql,Object.class);
        return this.addSelectMappedStatementForTable(mapperClass, METHOD_NAME, sqlSource, tableInfo);
    }

}
