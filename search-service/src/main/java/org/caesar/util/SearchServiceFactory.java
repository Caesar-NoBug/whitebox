package org.caesar.util;

import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.search.vo.Index;
import org.caesar.service.SearchService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SearchServiceFactory implements ApplicationContextAware {

    private final Map<DataSource, SearchService<Index>> serviceMap = new ConcurrentHashMap<>();

    public SearchService<Index> getSearchService(DataSource dataSource) {
        return serviceMap.get(dataSource);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, SearchService> tempMap = applicationContext.getBeansOfType(SearchService.class);
        tempMap.values().forEach(service -> serviceMap.put(service.getDataSource(), service));
    }

}
