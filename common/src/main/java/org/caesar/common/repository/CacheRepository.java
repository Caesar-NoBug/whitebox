package org.caesar.common.repository;

import org.redisson.api.*;
import org.springframework.data.redis.core.BoundZSetOperations;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

public interface CacheRepository {

    // 分布式自增唯一ID
    long nextId(String key);

    // 原子操作整形数据
    boolean deleteLong(String key);
    long getLongValue(String key);
    void setLongValue(String key, long value);
    long incrLong(String key);
    long decrLong(String key);

    // 对象相关
    <T> void setObject(String key, T object);
    <T> void setObject(String key, T object, int expire, TimeUnit timeUnit);
    boolean expire(String key, int expire, TimeUnit timeUnit);
    void deleteObject(String key);
    <T> T getObject(String key);
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
    RBloomFilter<Long> getBloomFilter(String key);

    // bitset
    RBitSet getBitSet(String key);

    // set相关
    <T> Set<T> getSet(String key);
    <T, V> BoundZSetOperations<T, V> getSortedSet(String key);
}
