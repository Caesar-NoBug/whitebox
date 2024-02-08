package org.caesar.common.util;
import org.caesar.common.cache.CacheRepository;
import org.redisson.api.RBitSet;
import org.redisson.api.RBloomFilter;

import java.util.List;

public class DataFilter {

    public static final String BLOOM_FILTER_SUFFIX = ":bloomFilter";
    public static final String REMOVED_BITSET_SUFFIX = ":removedBitset";

    /**
     * @param cacheRepository   缓存仓库
     * @param keyPrefix         数据缓存前缀
     * @param size              数据最大数量
     * @param falseProbability  容错率
     */
    public DataFilter(CacheRepository cacheRepository, String keyPrefix, int size, double falseProbability) {
        this.removedSet = cacheRepository.getBitSet(keyPrefix + REMOVED_BITSET_SUFFIX);
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
    private final RBloomFilter<Long> bloomFilter;

    // 被删除的文章保存在 bitmap 中
    private final RBitSet removedSet;

    public void init() {
        bloomFilter.tryInit(size, falseProbability);
    }

    public void add(long id) {
        bloomFilter.add(id);
    }

    public void add(List<Long> ids) {
        ids.forEach(bloomFilter::add);
    }

    public boolean contains(long id) {
        boolean contains = bloomFilter.contains(id);

        if(!contains) return false;

        // 判断该元素是否曾被删除
        return !removedSet.get(id);
    }

    public void remove(long id) {
        removedSet.set(id);
    }
}
