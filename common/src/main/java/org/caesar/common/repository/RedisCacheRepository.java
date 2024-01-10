package org.caesar.common.repository;

import org.caesar.common.redis.RedisCache;
import org.redisson.api.*;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisCacheRepository implements CacheRepository {

    @Resource
    private RedisCache redisCache;

    @Override
    public long nextId(String key) {
        return redisCache.nextId(key);
    }

    @Override
    public boolean deleteLong(String key) {
        return redisCache.getAtomicLong(key).delete();
    }

    @Override
    public long getLongValue(String key) {
        return redisCache.getAtomicLong(key).get();
    }

    @Override
    public void setLongValue(String key, long value) {
        redisCache.getAtomicLong(key).set(value);
    }

    @Override
    public long incrLong(String key) {
        return redisCache.getAtomicLong(key).incrementAndGet();
    }

    @Override
    public long decrLong(String key) {
        return redisCache.getAtomicLong(key).decrementAndGet();
    }

    @Override
    public <T> void setObject(String key, T value, int timeout, TimeUnit timeUnit) {
        redisCache.setCacheObject(key, value, timeout, timeUnit);
    }

    @Override
    public boolean expire(String key, int expire, TimeUnit timeUnit) {
        return redisCache.expire(key, expire, timeUnit);
    }

    @Override
    public void deleteObject(String key) {
        redisCache.deleteObject(key);
    }

    @Override
    public void setObject(String key, Object object) {
        redisCache.setCacheObject(key, object);
    }

    @Override
    public <T> T getObject(String key) {
        return redisCache.getCacheObject(key);
    }

    @Override
    public boolean exist(String key) {
        return redisCache.hasKey(key);
    }

    @Override
    public boolean deleteLogLog(String key) {
        return redisCache.getHyperLogLog(key).delete();
    }

    @Override
    public long getLogLogCount(String key) {
        return redisCache.getHyperLogLog(key).count();
    }

    @Override
    public boolean addLogLogElement(String key, Object object) {
        return redisCache.getHyperLogLog(key).add(object);
    }

    @Override
    public boolean addLogLogElements(String key, List<Object> object) {
        return redisCache.getHyperLogLog(key).addAll(object);
    }

    @Override
    public <T> RQueue<T> getQueue(String key) {
        return redisCache.getQueue(key);
    }

    @Override
    public RBloomFilter<Long> getBloomFilter(String key) {
        return redisCache.getBloomFilter(key);
    }

    @Override
    public RBitSet getBitSet(String key) {
        return redisCache.getBitSet(key);
    }

    @Override
    public <T> Set<T> getSet(String key) {
        return redisCache.getCacheSet(key);
    }

    @Override
    public<T, V> BoundZSetOperations<T, V> getSortedSet(String key) {
        return redisCache.getSortedSet(key);
    }

}
