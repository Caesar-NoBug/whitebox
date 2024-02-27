package org.caesar.common.util;
import org.caesar.common.cache.CacheRepository;
import org.redisson.api.RBitSet;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RSet;

import java.util.List;

public class DataFilter<T> {

    public static final String BLOOM_FILTER_SUFFIX = ":bloomFilter";
    public static final String REMOVED_BITSET_SUFFIX = ":removedSet";

    /**
     * @param cacheRepository   缓存仓库
     * @param keyPrefix         数据缓存前缀
     * @param size              数据最大数量
     * @param falseProbability  容错率
     */
    public DataFilter(CacheRepository cacheRepository, String keyPrefix, int size, double falseProbability) {
        this.removedSet = cacheRepository.getSet(keyPrefix + REMOVED_BITSET_SUFFIX);
        this.bloomFilter = cacheRepository.getBloomFilter(keyPrefix + BLOOM_FILTER_SUFFIX);
        this.size = size;
        this.falseProbability = falseProbability;
        // 初始化布隆过滤器
        init();
    }

    // 预计的最大文章数量
    private final Integer size;

    // 预计的最大文章数量
    private final Double falseProbability;

    // 文章布隆过滤器
    private final RBloomFilter<T> bloomFilter;

    // 被删除的文章保存在 bitmap 中
    private final RSet<T> removedSet;

    public void init() {
        bloomFilter.tryInit(size, falseProbability);
    }

    public void add(T id) {
        bloomFilter.add(id);
    }

    public void add(List<T> ids) {
        ids.forEach(bloomFilter::add);
    }

    public boolean contains(T key) {
        boolean contains = bloomFilter.contains(key);

        if(!contains) return false;

        // 判断该元素是否已被删除
        return !removedSet.contains(key);
    }

    public void remove(T id) {
        removedSet.add(id);
    }

}
