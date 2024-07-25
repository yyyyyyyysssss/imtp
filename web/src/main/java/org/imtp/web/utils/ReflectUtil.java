package org.imtp.web.utils;

import java.lang.reflect.Method;


public class ReflectUtil {

    //反射获取对象  必须要有无参构造器
    public static Object getNewInstance(Class<?> clazz){
        try {
            return clazz.getDeclaredConstructor().newInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    //根据方法名称反射调用 必须要有无参构造器
    public static Object invokeMethodByName(Class<?> clazz, String methodName, Object[] args) {
        Object obj = getNewInstance(clazz);
        return invokeMethodByName(obj,methodName,args);
    }

    //根据方法名称反射调用
    public static Object invokeMethodByName(Object obj, String methodName, Object[] args) {
        Method method = getAccessibleMethodByName(obj.getClass(), methodName, args);
        if (method == null){
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }
        try {
            return method.invoke(obj,args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Method getAccessibleMethodByName(Class<?> clazz, String methodName, Object[] args) {
        while (clazz != Object.class) {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                if (!method.getName().equals(methodName)) {
                    continue;
                }
                //无参方法
                if (args == null || args.length == 0) {
                    return method;
                }
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != args.length) {
                    continue;
                }
                boolean flag = true;
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class<?> parameterType = parameterTypes[i];
                    Object arg = args[i];
                    Class<?> aClass = arg.getClass();
                    if (!parameterType.equals(aClass)) {
                        flag = false;
                    }
                }
                if (flag) {
                    return method;
                }


            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

}
