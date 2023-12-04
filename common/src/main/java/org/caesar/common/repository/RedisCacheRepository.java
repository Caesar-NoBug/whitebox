package org.caesar.common.repository;

import org.caesar.common.redis.RedisCache;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
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
    public boolean deleteLogLog(String key) {
        return redisCache.getHyperLogLog(key).delete();
    }

    @Override
    public long getLogLogCount(String key) {
        return redisCache.getHyperLogLog(key).count();
    }

    @Override
    public void addLogLogElement(String key, Object object) {
        redisCache.getHyperLogLog(key).add(object);
    }

    @Override
    public void addLogLogElements(String key, List<Object> object) {
        redisCache.getHyperLogLog(key).addAll(object);
    }

}
