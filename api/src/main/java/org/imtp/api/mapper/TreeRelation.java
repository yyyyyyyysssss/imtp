package org.imtp.api.mapper;

public interface TreeRelation {

    default String parentFieldName(){
        return "parent_id";
    }

    default String childFieldName(){
        return "id";
    }

}
