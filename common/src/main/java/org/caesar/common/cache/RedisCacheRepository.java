package org.caesar.common.cache;

import org.caesar.common.exception.BusinessException;
import org.caesar.common.redis.RedisCache;
import org.caesar.common.util.ListUtil;
import org.caesar.common.vo.RefreshCacheTask;
import org.caesar.domain.common.enums.ErrorCode;
import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class RedisCacheRepository implements CacheRepository {

    // 用于生成唯一id
    private final Map<String, String> lastDate = new ConcurrentHashMap<>();

    // 默认缓存过期时间（10分钟）
    public final int DEFAULT_AVG_EXPIRE = 10 * 60;

    // 默认缓存最大过期时间（1小时）
    public final int DEFAULT_MAX_EXPIRE = 60 * 60;

    // 刷新缓存过期时机：过期前10秒
    public static final int REFRESH_CACHE_START_TIME = 10 * 1000;

    // 刷新缓存间隔时间：2秒
    public static final int REFRESH_CACHE_INTERVAL = 2 * 1000;

    public static final String REFRESH_CACHE_SCRIPT = "local keys = KEYS\n" +
            "for i = 1, #keys do\n" +
            "   redis.call('EXPIRE', KEYS[i], ARGV[i])" +
            "end";

    // 缓存是否被访问
    private final Set<String> cacheAccessed = ConcurrentHashMap.newKeySet();

    // 缓存过期信息
    private final List<RefreshCacheTask> refreshCacheTasks = new LinkedList<>();

    private final Random random = new Random();

    private final Runnable doNothing = () -> {
    };

    @Resource
    private RedisCache redisCache;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    public <T> T cache(String key, int avgExpire, int maxExpire, Supplier<T> supplier, Runnable afterExpireTask) {

        T result = getObject(key);

        // 如果缓存存在，直接返回缓存数据并标记缓存被访问过
        if (Objects.nonNull(result)) {
            cacheAccessed.add(key);
            return result;
        }

        // 当缓存不存在时，尝试获取锁并让成功获取锁的线程执行获取缓存的逻辑
        // 当获取锁失败时，尝试直接获取缓存
        if (!tryLock(key, 0, 10, TimeUnit.SECONDS)) {
            long waitTime = 0;

            final long sleepTime = 500L;

            while (true) {
                try {
                    Thread.sleep(sleepTime);

                    waitTime += sleepTime;

                    result = getObject(key);

                    // 获取成功则直接返回
                    if (Objects.nonNull(result)) {
                        cacheAccessed.add(key);
                        return result;
                    }

                    final int maxWaitTime = 5 * 1000;
                    // 当重试超过5秒时，说明添加缓存失败，抛出异常
                    if (waitTime > maxWaitTime) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Wait too long time for cache timeout.");
                    }

                } catch (InterruptedException e) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Interrupted when waiting for cache.", e);
                }
            }
        }
        // 处理获取数据的逻辑
        result = supplier.get();

        if (Objects.isNull(result)) {
            return null;
        }

        // 获取数据
        int expire = avgExpire + random.nextInt(avgExpire);

        setObject(key, result, expire, TimeUnit.SECONDS);

        long now = System.currentTimeMillis();

        // 创建缓存自动更新过期时间任务
        RefreshCacheTask task = new RefreshCacheTask(key, now + expire * 1000L - REFRESH_CACHE_START_TIME,
                now + maxExpire * 1000L, avgExpire, afterExpireTask);

        ListUtil.binaryInsert(refreshCacheTasks, task);

        return result;
    }

    @Override
    public <T> T cache(String key, int avgExpire, int maxExpire, Supplier<T> supplier) {
        return cache(key, avgExpire, maxExpire, supplier, doNothing);
    }

    @Override
    public <T> T cache(String key, Supplier<T> supplier, Runnable afterExpireTask) {
        return cache(key, DEFAULT_AVG_EXPIRE, DEFAULT_AVG_EXPIRE, supplier, afterExpireTask);
    }

    @Override
    public <T> T cache(String key, Supplier<T> supplier) {
        return cache(key, DEFAULT_AVG_EXPIRE, DEFAULT_MAX_EXPIRE, supplier, doNothing);
    }

    // 每2秒刷新缓存过期时间
    @Scheduled(fixedRate = REFRESH_CACHE_INTERVAL)
    public void refreshCache() {

        // 需要执行的任务
        List<RefreshCacheTask> todoRefreshExpireTasks = new ArrayList<>();

        // 执行过期任务
        List<Runnable> afterExpireTasks = new ArrayList<>();

        long now = System.currentTimeMillis();
        for (int i = refreshCacheTasks.size() - 1; i >= 0; i--) {

            RefreshCacheTask task = refreshCacheTasks.get(i);

            // 如果尚未到开始刷新的时间，则结束循环
            if (now < task.getStartTime()) break;

            refreshCacheTasks.remove(i);

            // 如果超过最大缓存时间或缓存未被访问，则删除缓存
            if (now > task.getEndTime() || !cacheAccessed.contains(task.getKey())) {
                afterExpireTasks.add(task.getBeforeExpireTask());
                continue;
            }

            // 如果为超过最大缓存时间且缓存被访问了，则更新缓存过期时间
            todoRefreshExpireTasks.add(task);
        }

        int size = todoRefreshExpireTasks.size();
        List<Object> keys = new ArrayList<>(size);
        Object[] expires = new Object[size];

        // 更新缓存过期时间
        for (int i = 0; i < size; i++) {
            // 获取执行更新操作的信息
            RefreshCacheTask task = todoRefreshExpireTasks.get(i);
            keys.add(task.getKey());
            expires[i] = task.getExpire();

            // 更新刷新缓存任务并保持集合有序
            cacheAccessed.remove(task.getKey());
            task.refreshExpire();
            ListUtil.binaryInsert(refreshCacheTasks, task);
        }

        eval(REFRESH_CACHE_SCRIPT, keys, expires);

        for (Runnable task : afterExpireTasks) {
            taskExecutor.execute(task);
        }

    }

    @Override
    public long nextId(String key) {
        LocalDateTime now = LocalDateTime.now();
        //2023-01-01 00:00
        long BEGIN_TIMESTAMP = 1672531200L;
        long timestamp = now.toEpochSecond(ZoneOffset.UTC) - BEGIN_TIMESTAMP;

        String curr = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        String last = lastDate.get(key);

        RAtomicLong cacheNumber = redissonClient.getAtomicLong(key + curr);

        //新的一天添加新的主键并更新lastDate
        if (Objects.isNull(last) || !last.equals(curr)) {
            lastDate.put(key, curr);
            //设置有效期避免占用过多资源
            cacheNumber.set(0);
            cacheNumber.expire(14, TimeUnit.DAYS);
        }

        long count = cacheNumber.incrementAndGet();

        return timestamp << 32 + count;
    }

    @Override
    public RLock getLock(String key) {
        return redissonClient.getLock("lock:" + key);
    }

    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit) {
        try {
            return redissonClient.getLock("lock:" + key).tryLock(waitTime, leaseTime, timeUnit);
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Redis try lock was interrupted.", e);
        }
    }

    @Override
    public void unlock(String key) {
        redissonClient.getLock(key).unlock();
    }

    @Override
    public boolean deleteLong(String key) {
        return redissonClient.getAtomicLong(key).delete();
    }

    @Override
    public long getLongValue(String key) {
        return redissonClient.getAtomicLong(key).get();
    }

    @Override
    public void setLongValue(String key, long value) {
        redissonClient.getAtomicLong(key).set(value);
    }

    @Override
    public long incrLong(String key) {
        return redissonClient.getAtomicLong(key).incrementAndGet();
    }

    @Override
    public long decrLong(String key) {
        return redissonClient.getAtomicLong(key).decrementAndGet();
    }

    @Override
    public <T> void setObject(String key, T value, int timeout, TimeUnit timeUnit) {
        redisCache.setCacheObject(key, value, timeout, timeUnit);
    }

    @Override
    public <T> void setObject(String key, T object, int expire) {
        redisCache.setCacheObject(key, object, expire, TimeUnit.SECONDS);
    }

    @Override
    public boolean expire(String key, int expire, TimeUnit timeUnit) {
        return redisCache.expire(key, expire, timeUnit);
    }

    @Override
    public long getExpire(String key) {
        return redisCache.getExpire(key);
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
        return redissonClient.getHyperLogLog(key).delete();
    }

    @Override
    public long getLogLogCount(String key) {
        return redissonClient.getHyperLogLog(key).count();
    }

    @Override
    public boolean addLogLogElement(String key, Object object) {
        return redissonClient.getHyperLogLog(key).add(object);
    }

    @Override
    public String eval(String script, List<Object> keys, Object[] args) {
        return redissonClient.getScript(new StringCodec()).eval(RScript.Mode.READ_WRITE, script, RScript.ReturnType.VALUE, keys, args);
    }

    @Override
    public <T> RQueue<T> getQueue(String key) {
        return redissonClient.getQueue(key);
    }

    @Override
    public RBloomFilter<Long> getBloomFilter(String key) {
        return redissonClient.getBloomFilter(key);
    }

    @Override
    public RBitSet getBitSet(String key) {
        return redissonClient.getBitSet(key);
    }

    @Override
    public <T> Set<T> getSet(String key) {
        return redissonClient.getSet(key);
    }

    @Override
    public <T, V> BoundZSetOperations<T, V> getSortedSet(String key) {
        return redisCache.getSortedSet(key);
    }

}