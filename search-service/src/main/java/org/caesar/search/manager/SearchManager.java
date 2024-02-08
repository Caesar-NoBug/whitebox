package org.caesar.search.manager;

import org.caesar.domain.common.vo.PageVO;
import org.caesar.common.cache.CacheRepository;
import org.caesar.search.constant.RedisPrefix;
import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.search.enums.SortField;
import org.caesar.domain.search.vo.IndexVO;
import org.caesar.search.service.SearchService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class SearchManager implements ApplicationContextAware {

    private final Map<DataSource, SearchService<?>> serviceMap = new ConcurrentHashMap<>();

    private SearchService<?> getService(DataSource dataSource) {
        return serviceMap.get(dataSource);
    }

    @Resource
    private CacheRepository cacheRepo;

    //TODO: 可以用多线程优化
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, SearchService> tempMap = applicationContext.getBeansOfType(SearchService.class);
        tempMap.values().forEach(service -> serviceMap.put(service.getDataSource(), service));
    }

    // 统一缓存搜索结果
    public PageVO<? extends IndexVO> search(DataSource dataSource, String text, int from, int size) {

        PageVO<? extends IndexVO> result;
        String cacheKey = String.format(RedisPrefix.CACHE_SEARCH_RESULT, dataSource, text);

        return cacheRepo.cache(cacheKey,
                () -> getService(dataSource).search(text, from, size));
    }

    public PageVO<? extends IndexVO> sortSearch(DataSource dataSource, String text, SortField field, int from, int size) {
        PageVO<? extends IndexVO> result;
        String cacheKey = String.format(RedisPrefix.CACHE_SORT_SEARCH_RESULT, dataSource, field.getValue(), text);
        result = cacheRepo.getObject(cacheKey);

        // 排序检索相同的概率要更低，设置更低的缓存时间
        if (Objects.nonNull(result)) {
            cacheRepo.expire(cacheKey, 5, TimeUnit.MINUTES);
            return result;
        }

        result = getService(dataSource).sortSearch(text, field, from, size);

        int expire = (int) (2 + (Math.random() * 6));
        cacheRepo.setObject(cacheKey, result, expire, TimeUnit.MINUTES);

        return result;
    }

    public List<String> suggestion(DataSource dataSource, String text, int size) {

        List<String> suggestion;
        String cacheKey = String.format(RedisPrefix.CACHE_SUGGESTION, dataSource, text);
        suggestion = cacheRepo.getObject(cacheKey);

        // 排序检索相同的概率要更低，设置更低的缓存时间
        if (Objects.nonNull(suggestion)) {
            cacheRepo.expire(cacheKey, 5, TimeUnit.MINUTES);
            return suggestion;
        }

        suggestion = getService(dataSource).suggestion(text, size);

        int expire = (int) (2 + (Math.random() * 6));
        cacheRepo.setObject(cacheKey, suggestion, expire, TimeUnit.MINUTES);

        return suggestion;
    }

    public void insertIndex(DataSource dataSource, List indices) {
        getService(dataSource).insertIndex(indices);
    }

    public void deleteIndex(DataSource dataSource, List<Long> ids) {
        getService(dataSource).deleteIndex(ids);
    }

    public List<Object> searchBatch(List<String> texts, int size, DataSource dataSource) {

        List<Object> result = new ArrayList<>();
        SearchService<?> service = getService(dataSource);

        for (String text : texts) {
            result.addAll(service.search(text, 0, size).getData());
        }

        return result;
    }

    public Map<DataSource, PageVO<? extends IndexVO>> searchAggregation(String text, int from, int size) {

        Map<DataSource, PageVO<? extends IndexVO>> result = new HashMap<>();

        serviceMap.forEach((source, service) -> {
            result.put(source, service.search(text, from, size));
        });

        return result;
    }
}
