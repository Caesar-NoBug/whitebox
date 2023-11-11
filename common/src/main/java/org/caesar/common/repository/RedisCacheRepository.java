package org.caesar.common.repository;

import org.caesar.common.util.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RedisCacheRepository implements CacheRepository {

    @Autowired
    private RedisCache redisCache;

    @Override
    public long nextId(String key) {
        return redisCache.nextId(key);
    }

    @Override
    public void deleteCacheObject(String key) {
        redisCache.deleteObject(key);
    }

    @Override
    public void setCacheObject(String key, Object object) {
        redisCache.setCacheObject(key, object);
    }

    @Override
    public Object getCacheObject(String key) {
        return redisCache.getCacheObject(key);
    }

}
