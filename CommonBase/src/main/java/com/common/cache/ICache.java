package com.common.cache;

/**
 * 缓存接口
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
interface ICache<K, V> {

    /**
     * 存入缓存
     *
     * @param key
     * @param value
     */
    void put(K key, V value);

    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    V get(K key);

    /**
     * 删除缓存
     *
     * @param key
     */
    void remove(K key);

    /**
     * 缓存中是否存在
     *
     * @param key
     * @return
     */
    boolean contains(K key);

    /**
     * 清空缓存
     */
    void clear();
}
