package com.common.utils.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射工具类
 * 通过反射获得对应函数功能
 *
 * @author Wangxx
 * @date 2016/12/29
 */
public class ReflectionUtil {

    /**
     * 通过类对象，运行指定方法
     *
     * @param obj        类对象
     * @param methodName 方法名
     * @param params     参数值
     *
     * @return 失败返回null
     */
    public static Object invokeMethod(Object obj, String methodName, Object[] params) {
        if (obj == null || TextUtils.isEmpty(methodName)) {
            return null;
        }
        Class<?> clazz = obj.getClass();
        try {
            Class<?>[] paramTypes = null;
            if (params != null) {
                paramTypes = new Class[params.length];
                for (int i = 0; i < params.length; ++i) {
                    paramTypes[i] = params[i].getClass();
                }
            }
            Method method = clazz.getMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(obj, params);
        } catch (NoSuchMethodException e) {
            Log.i("reflect", "method " + methodName + " not found in " + obj.getClass().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取类的变量值
     *
     * @param obj       类对象
     * @param fieldName 变量名
     *
     * @return
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        if (obj == null || TextUtils.isEmpty(fieldName)) {
            return null;
        }
        Class<?> clazz = obj.getClass();
        while (clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            clazz = clazz.getSuperclass();
        }
        Log.e("reflect", "get field " + fieldName + " not found in " + obj.getClass().getName());
        return null;
    }

    /**
     * 设置类的变量值
     *
     * @param obj        类对象
     * @param fieldName  变量名
     * @param fieldValue 变量值
     */
    public static void setFieldValue(Object obj, String fieldName, Object fieldValue) {
        if (obj == null || TextUtils.isEmpty(fieldName)) {
            return;
        }
        Class<?> clazz = obj.getClass();
        while (clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(obj, fieldValue);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * 获取方法
     *
     * @param propertiesName
     * @param object
     *
     * @return
     */
    public static Object invokeMethod(String propertiesName, Object object) {
        try {
            if (object == null) {
                return null;
            }
            if (!propertiesName.contains(".")) {
                String methodName = "get" + getMethodName(propertiesName);
                Method method = object.getClass().getMethod(methodName);
                return method.invoke(object);
            }
            String methodName = "get" + getMethodName(propertiesName.substring(0, propertiesName.indexOf(".")));
            Method method = object.getClass().getMethod(methodName);
            return invokeMethod(propertiesName.substring(propertiesName.indexOf(".") + 1), method.invoke(object));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取方法名
     *
     * @param fieldName
     *
     * @return
     */
    private static String getMethodName(String fieldName) {
        byte[] items = fieldName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }

    /**
     * 通过反射获取指定全路径类名的intent对象
     *
     * @param context
     * @param className 全路径类名
     *
     * @return
     */
    public static Intent getIntent(Context context, String className) {
        Intent intent = new Intent();
        try {
            Class<?> clazz = Class.forName(className);
            intent.setClass(context, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }
}