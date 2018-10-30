package com.common.utils.utils;

import android.os.Build;
import android.support.v4.util.LongSparseArray;
import android.support.v4.util.SimpleArrayMap;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * 判空相关工具类
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public class EmptyUtil {

    private EmptyUtil() {}

    /**
     * 判断map集合是否为空
     *
     * @param map
     *
     * @return 如果map为null或size为0，返回true
     */
    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断map集合是否非空
     *
     * @param map
     *
     * @return 如果map不为null且size大于0，返回true
     */
    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    /**
     * 判断Collection集合是否为空
     *
     * @param collection
     *
     * @return 如果Collection为null或size为0，返回true
     */
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断Collection集合是否非空
     *
     * @param collection
     *
     * @return 如果Collection不为null且size大于0，返回true
     */
    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     *
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 判断字符串是否非空
     *
     * @param str
     *
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断所有集合中是否包含空
     *
     * @param collections
     *
     * @return 如果Collection[]为null或length等于0，或Collection[]中的Collection为null或size为0，返回true
     */
    public static boolean containsEmpty(Collection... collections) {
        if (collections == null || collections.length == 0) {
            return true;
        }
        for (Collection collection : collections) {
            if (collection == null || collection.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断所有集合是否全部非空
     *
     * @param collections
     *
     * @return 如果Collection[]不为null且length大于0，且Collection[]中的全部Collection都不为null且size大于0，返回true
     */
    public static boolean isAllNonEmpty(Collection... collections) {
        return !containsEmpty(collections);
    }

    /**
     * 判断所有list集合中是否包含非空
     *
     * @param collections
     *
     * @return 如果list[]不为null且length大于0，且list[]中list存在不为null且size大于0，返回true
     */
    public static boolean containsNonEmpty(Collection... collections) {
        if (collections != null && collections.length > 0) {
            for (Collection collection : collections) {
                if (collection != null && !collection.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断所有list集合是否全部为空
     *
     * @param collections
     *
     * @return
     */
    public static boolean isAllEmpty(Collection... collections) {
        return !containsNonEmpty(collections);
    }

    /**
     * 判断是否包含空字符串
     *
     * @param strings
     *
     * @return
     */
    public static boolean containsEmpty(String... strings) {
        if (strings == null || strings.length == 0) {
            return true;
        }
        for (String str : strings) {
            if (str == null || str.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否所有字符串非空
     *
     * @param strings
     *
     * @return
     */
    public static boolean isAllNonEmpty(String... strings) {
        return !containsEmpty(strings);
    }

    /**
     * 判断是否包含非空字符串
     *
     * @param strings
     *
     * @return
     */
    public static boolean containsNonEmpty(String... strings) {
        if (strings != null && strings.length > 0) {
            for (String str : strings) {
                if (str != null && !str.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否所有字符串为空
     *
     * @param strings
     *
     * @return
     */
    public static boolean isAllEmpty(String... strings) {
        return !containsNonEmpty(strings);
    }

    /**
     * 判断对象是否为空（可判断--字符串、数组，集合）
     *
     * @param obj 对象
     *
     * @return 如果obj为null或size(length)为0，返回true
     */
    public static boolean isEmpty(final Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String && obj.toString().length() == 0) {        // 字符串
            return true;
        }
        if (obj.getClass().isArray() && Array.getLength(obj) == 0) {        // 数组
            return true;
        }
        if (obj instanceof Collection && ((Collection) obj).isEmpty()) {    // 集合（包含list和set）
            return true;
        }
        if (obj instanceof Map && ((Map) obj).isEmpty()) {                  // map键值对集合
            return true;
        }
        if (obj instanceof SimpleArrayMap && ((SimpleArrayMap) obj).isEmpty()) {
            return true;
        }
        if (obj instanceof SparseArray && ((SparseArray) obj).size() == 0) {
            return true;
        }
        if (obj instanceof SparseBooleanArray && ((SparseBooleanArray) obj).size() == 0) {
            return true;
        }
        if (obj instanceof SparseIntArray && ((SparseIntArray) obj).size() == 0) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (obj instanceof SparseLongArray && ((SparseLongArray) obj).size() == 0) {
                return true;
            }
        }
        if (obj instanceof LongSparseArray && ((LongSparseArray) obj).size() == 0) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (obj instanceof android.util.LongSparseArray && ((android.util.LongSparseArray) obj).size() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断对象是否非空
     *
     * @param obj 对象
     *
     * @return 如果obj不为null且size(length)大于0，返回true
     */
    public static boolean isNotEmpty(final Object obj) {
        return !isEmpty(obj);
    }
}
