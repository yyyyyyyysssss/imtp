package org.imtp.web.mapper;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.session.Configuration;

import java.util.List;


public class MySqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(new Configuration(),mapperClass, tableInfo);
        if (TreeMapper.class.isAssignableFrom(mapperClass)){
            methodList.add(new SelectChildrenBatchByIds());
            methodList.add(new SelectParentBatchByIds());
        }
        return methodList;
    }
}
