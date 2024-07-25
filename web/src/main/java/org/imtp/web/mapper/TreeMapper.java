package org.imtp.web.mapper;

import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

//用于包含树结构的表数据查询与操作
public interface TreeMapper<T extends TreeRelation> {

    //递归查询子节点（包含自身）
    List<T> selectChildrenBatchByIds(@Param("coll") Collection<? extends Serializable> idList);

    //递归查询父节点（包含自身）
    List<T> selectParentBatchByIds(@Param("coll") Collection<? extends Serializable> idList);

}
