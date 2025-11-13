package org.imtp.api.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/17 21:36
 */
public class TreeUtils {

    public static <T,ID extends Serializable> List<T> buildTree(
            List<T> list,
            Function<T,ID> idGetter,
            Function<T,ID> parentGetter,
            BiConsumer<T,List<T>> childrenSetter,
            ID rootParentId
    )
    {
        Object nullKey = new Object();
        //根节点
        Map<Object, List<T>> parentGroupMap = list.stream().collect(Collectors.groupingBy(item -> {
            ID pid  = parentGetter.apply(item);
            return pid != null ? pid : nullKey;
        }, LinkedHashMap::new, Collectors.toList()));
        return buildSubTree(rootParentId,parentGroupMap,idGetter,childrenSetter,nullKey);
    }

    private static <T,ID extends Serializable> List<T> buildSubTree(
            ID parentId,
            Map<Object, List<T>> parentGroupMap,
            Function<T,ID> idGetter,
            BiConsumer<T,List<T>> childrenSetter,
            Object nullKey
    ){
        Object key = parentId != null ? parentId : nullKey;
        List<T> children = parentGroupMap.getOrDefault(key, new ArrayList<>());
        for (T child : children){
            ID childId  = idGetter.apply(child);
            List<T> subChildren = buildSubTree(childId,parentGroupMap,idGetter,childrenSetter,nullKey);
            childrenSetter.accept(child,subChildren);
        }
        return children;
    }

}
