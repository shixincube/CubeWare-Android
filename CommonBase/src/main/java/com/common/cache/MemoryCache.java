package com.common.cache;

import android.support.v4.util.LruCache;

/**
 * 内存缓存
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public class MemoryCache<K, V> implements ICache<K, V> {

    private LruCache<K, V> cache;

    private MemoryCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        cache = new LruCache<>(cacheSize);
    }

    public static MemoryCache getInstance() {
        return MemoryCacheHolder.INSTANCE;
    }

    private static class MemoryCacheHolder {
        private static final MemoryCache INSTANCE = new MemoryCache();
    }

    @Override
    public void put(K key, V value) {
        if (cache.get(key) != null) {
            cache.remove(key);
        }
        cache.put(key, value);
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void remove(K key) {
        if (cache.get(key) != null) {
            cache.remove(key);
        }
    }

    @Override
    public boolean contains(K key) {
        return cache.get(key) != null;
    }

    @Override
    public void clear() {
        cache.evictAll();
    }
}
