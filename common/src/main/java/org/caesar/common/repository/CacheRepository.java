package org.caesar.common.repository;

public interface CacheRepository {
    long nextId(String key);
    <T> void setCacheObject(String key, T object);
    void deleteCacheObject(String key);
    <T> T getCacheObject(String key);
}
