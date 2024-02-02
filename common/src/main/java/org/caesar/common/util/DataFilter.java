package org.caesar.common.util;
import org.caesar.common.cache.CacheRepository;

import java.util.List;

public class DataFilter {

    /**
     * @param cacheRepository   缓存仓库
     * @param bloomFilterKey    布隆过滤器对应的key
     * @param removedSetKey     删除的元素集合对应的key
     * @param size              数据最大数量
     * @param falseProbability  容错率
     */
    public DataFilter(CacheRepository cacheRepository, String bloomFilterKey, String removedSetKey, int size, double falseProbability) {
        this.cacheRepo = cacheRepository;
        this.bloomFilterKey = bloomFilterKey;
        this.removedSetKey = removedSetKey;
        this.size = size;
        this.falseProbability = falseProbability;
    }

    // 文章布隆过滤器
    private final String bloomFilterKey;

    // 被删除的文章保存在 bitmap 中
    private final String removedSetKey;

    // 预计的最大文章数量
    private final Integer size;

    // 预计的最大文章数量
    private final Double falseProbability;

    private final CacheRepository cacheRepo;

    public void init() {
        cacheRepo.getBloomFilter(bloomFilterKey).tryInit(size, falseProbability);
    }

    public void add(long id) {
        cacheRepo.getBloomFilter(bloomFilterKey).add(id);
    }

    public void add(List<Long> ids) {
        ids.forEach(id -> cacheRepo.getBloomFilter(bloomFilterKey).add(id));
    }

    public boolean contains(long id) {
        boolean contains = cacheRepo.getBloomFilter(bloomFilterKey).contains(id);

        if(!contains) return false;

        // 判断该元素是否曾被删除
        return !cacheRepo.getBitSet(removedSetKey).get(id);
    }

    public void remove(long id) {
        cacheRepo.getBitSet(removedSetKey).set(id);
    }

}
