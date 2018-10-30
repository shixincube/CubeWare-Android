package com.common.utils.manager;

import java.util.HashMap;
import java.util.Map;

/**
 * 单例容器管理
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public class SingletonManager {
    private static Map<String, Object> instanceMap = new HashMap<>();

    private SingletonManager() {
    }

    public static void registerInstance(String key, Object instance) {
        instanceMap.put(key, instance);
    }

    public static Object getInstance(String key) {
        return instanceMap.get(key);
    }

    public static Map<String, Object> getInstanceMap() {
        return new HashMap<>(instanceMap);
    }

    public static boolean containsKey(String key) {
        return instanceMap.containsKey(key);
    }

    public static boolean containsValue(Object instance) {
        return instanceMap.containsValue(instance);
    }

    public static boolean isEmpty() {
        return instanceMap.isEmpty();
    }

    public static int size() {
        return instanceMap.size();
    }

    public static void removeInstance(String key) {
        if (instanceMap.containsKey(key)) {
            instanceMap.remove(key);
        }
    }

    public static void clearAll() {
        instanceMap.clear();
    }
}

