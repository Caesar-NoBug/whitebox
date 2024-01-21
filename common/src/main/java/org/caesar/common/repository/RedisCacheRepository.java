package org.caesar.common.repository;

import org.caesar.common.redis.RedisCache;
import org.redisson.api.*;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class RedisCacheRepository implements CacheRepository {
    // 默认最小过期时间（10分钟）
    public static final int DEFAULT_MIN_EXPIRE = 600;
    // 默认过期浮动时间（10分钟）
    public static final int DEFAULT_EXPIRE_FLOAT = 600;

    public final String GET_AND_EXPIRE_LUA_SCRIPT = "local res = redis.call('GET', KEYS[1]);\n"
            + "if(res ~= nil)\n" +
            "then\n" +
            "   redis.call('EXPIRE', KEYS[1], ARGV[1]);" +
            "end" +
            "return res;";

    private final Random random = new Random();

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
    public <T> T handleCache(String key, int minExpire, int expireFloat, Supplier<T> supplier) {

        T result = getObject(key);

        // 如果缓存存在，直接返回缓存数据并更新缓存存活时间
        if(Objects.nonNull(result)) {
            // 设置缓存过期时间为最大值
            expire(key, minExpire + expireFloat, TimeUnit.SECONDS);
        } else {
            // 如果缓存不存在，则获取数据并设置缓存
            result = supplier.get();

            if(Objects.isNull(result)) {
                return null;
            }

            int expire = minExpire + random.nextInt(expireFloat);

            setObject(key, result, expire, TimeUnit.SECONDS);
        }

        return result;
    }

    @Override
    public <T> T handleCache(String key, Supplier<T> supplier) {
        return handleCache(key, DEFAULT_MIN_EXPIRE, DEFAULT_EXPIRE_FLOAT, supplier);
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
    public String eval(String script, List<Object> keys, Object[] args) {
        return redisCache.getScript().eval(RScript.Mode.READ_WRITE, script, RScript.ReturnType.VALUE, keys, args);
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
