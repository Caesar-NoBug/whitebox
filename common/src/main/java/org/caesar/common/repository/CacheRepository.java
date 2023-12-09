package org.caesar.common.repository;

import org.redisson.api.RAtomicLong;

import java.util.List;
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

    // 基数统计相关
    boolean deleteLogLog(String key);
    long getLogLogCount(String key);
    void addLogLogElement(String key, Object object);
    void addLogLogElements(String key, List<Object> object);
}
