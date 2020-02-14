package com.common.utils;

/**
 * 判等相关工具类
 *
 * @author liufeng
 * @date 2017-10-21
 */
public class EqualsUtil {

    private EqualsUtil() {}

    /**
     * 判断两个对象是否相等
     *
     * @param objectA
     * @param objectB
     *
     * @return
     */
    public static boolean equals(Object objectA, Object objectB) {
        if (objectA == null && objectB == null) {
            return true;
        }
        else if (objectA == null) {
            return false;
        }
        else if (objectB == null) {
            return false;
        }
        else {
            return objectA.equals(objectB);
        }
    }

    /**
     * 判断两个对象是否不相等
     *
     * @param objectA
     * @param objectB
     *
     * @return
     */
    public static boolean notEquals(Object objectA, Object objectB) {
        return !equals(objectA, objectB);
    }

    /**
     * 将两个对象转为json字符串判断是否相等
     *
     * @param objectA
     * @param objectB
     *
     * @return
     */
    public static boolean toJsonEquals(Object objectA, Object objectB) {
        if (objectA == null && objectB == null) {
            return true;
        }
        else if (objectA == null) {
            return false;
        }
        else if (objectB == null) {
            return false;
        }
        else {
            return GsonUtil.toJson(objectA).equals(GsonUtil.toJson(objectB));
        }
    }

    /**
     * 将两个对象转为json字符串判断是否不等
     *
     * @param objectA
     * @param objectB
     *
     * @return
     */
    public static boolean toJsonNotEquals(Object objectA, Object objectB) {
        return !toJsonEquals(objectA, objectB);
    }

    /**
     * 将json字符串和转为json字符串后的对象进行比较
     *
     * @param jsonStr
     * @param object
     *
     * @return
     */
    public static boolean toJsonEquals(String jsonStr, Object object) {
        if (jsonStr == null && object == null) {
            return true;
        }
        else if (jsonStr == null) {
            return false;
        }
        else if (object == null) {
            return false;
        }
        else {
            return jsonStr.equals(GsonUtil.toJson(object));
        }
    }

    /**
     * 将json字符串和转为json字符串后的对象进行比较
     *
     * @param jsonStr
     * @param objectB
     *
     * @return
     */
    public static boolean toJsonNotEquals(String jsonStr, Object objectB) {
        return !toJsonEquals(jsonStr, objectB);
    }
}
