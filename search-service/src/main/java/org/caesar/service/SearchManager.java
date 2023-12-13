package org.caesar.service;

import org.caesar.common.model.vo.PageVO;
import org.caesar.common.repository.CacheRepository;
import org.caesar.constant.RedisPrefix;
import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.search.enums.SortField;
import org.caesar.domain.search.vo.Index;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class SearchManager implements ApplicationContextAware {

    private final Map<DataSource, SearchService<? extends Index>> serviceMap = new ConcurrentHashMap<>();

    private SearchService<? extends Index> getService(DataSource dataSource) {
        return serviceMap.get(dataSource);
    }

    @Resource
    private CacheRepository cacheRepo;

    // TODO: 用模板方法模式重构，统一处理缓存
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, SearchService> tempMap = applicationContext.getBeansOfType(SearchService.class);
        tempMap.values().forEach(service -> serviceMap.put(service.getDataSource(), service));
    }

    // 统一处理搜索结果的缓存
    public PageVO<? extends Index> search(DataSource dataSource, String text, int from, int size) {

        PageVO<? extends Index> result;
        String cacheKey = String.format(RedisPrefix.CACHE_SEARCH_RESULT, dataSource, text);
        result = cacheRepo.getObject(cacheKey);

        if (Objects.nonNull(result)) {
            cacheRepo.expire(cacheKey, 15, TimeUnit.MINUTES);
            return result;
        }

        result = getService(dataSource).search(text, from, size);

        int expire = (int) (5 + (Math.random() * 10));
        cacheRepo.setObject(cacheKey, result, expire, TimeUnit.MINUTES);

        return result;
    }

    public PageVO<? extends Index> sortSearch(DataSource dataSource, String text, SortField field, int from, int size) {
        PageVO<? extends Index> result;
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

    public boolean insertIndex(DataSource dataSource, List indices) {
        return getService(dataSource).insertIndex(indices);
    }

    public boolean deleteIndex(DataSource dataSource, List<Long> ids) {
        return getService(dataSource).deleteIndex(ids);
    }

}
