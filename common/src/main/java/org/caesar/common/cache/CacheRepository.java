package org.caesar.common.cache;

import org.redisson.api.*;
import org.springframework.data.redis.core.BoundZSetOperations;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public interface CacheRepository {

    // 分布式自增唯一ID
    long nextId(String key);

    // 分布式锁相关
    RLock getLock(String key);
    boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException;
    void unlock(String key);

    void deleteObject(List<String> keys);

    // 原子操作整形数据
    boolean deleteLong(String key);
    long getLongValue(String key);
    void setLongValue(String key, long value);
    long incrLong(String key, long delta);
    long decrLong(String key);

    // 对象相关
    <T> T getObject(String key);

    // 更新对象，保留原来的ttl
    <T> void updateObject(String key, T value);

    // 设置对象，若为设置ttl则永久保留
    <T> void setObject(String key, T value);
    <T> void setObject(String key, T value, long expire, TimeUnit timeUnit);
    <T> void setObject(String key, T value, long expire);
    boolean deleteObject(String key);

    // 过期时间相关
    boolean expire(String key, int expire, TimeUnit timeUnit);
    long getExpire(String key);

    /**
     * @param key           缓存key
     * @param avgExpire     平均过期时间(默认10分钟)
     * @param maxExpire     最大过期时间
     * @param supplier      实际数据的提供者
     * @param onExpire      数据过期时的回调函数
     * @param <T>           数据类型
     * @return              缓存中或supplier的数据
     */
    <T> T cache(String key, int avgExpire, int maxExpire, int visitThreshold, Supplier<T> supplier, Runnable onExpire);

    <T> T cache(String key, int avgExpire, int maxExpire, int visitThreshold, Supplier<T> supplier);

    <T> T cache(String key, int minExpire, int expireFloat, Supplier<T> supplier);

    <T> T cache(String key, Supplier<T> supplier, Runnable onExpire);

    <T> T cache(String key, int visitThreshold, Supplier<T> supplier);

    <T> T cache(String key, Supplier<T> supplier);

    boolean exist(String key);

    // 基数统计相关
    boolean deleteLogLog(String key);
    long getLogLogCount(String key);
    boolean addLogLogElement(String key, Object object);

    // lua表达式
    String eval(String script, List<Object> keys, Object[] args);

    // 队列相关
    <T> RQueue<T> getQueue(String key);

    // 布隆过滤器
    <T> RBloomFilter<T> getBloomFilter(String key);

    // bitset
    RBitSet getBitSet(String key);

    // set相关
    <T> RSet<T> getSet(String key);
    <T, V> BoundZSetOperations<T, V> getSortedSet(String key);
}
