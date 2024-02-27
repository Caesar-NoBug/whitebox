package org.caesar.search.repository.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.caesar.domain.search.enums.DataSource;
import org.caesar.search.mapper.SearchHistoryMapper;
import org.caesar.search.model.MsHistoryStruct;
import org.caesar.search.model.entity.SearchHistory;
import org.caesar.search.model.po.SearchHistoryPO;
import org.caesar.search.repository.SearchHistoryRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class SearchHistoryRepositoryImpl extends ServiceImpl<SearchHistoryMapper, SearchHistoryPO> implements SearchHistoryRepository {

    @Resource
    private MsHistoryStruct historyStruct;

    @Override
    public List<SearchHistory> getSearchHistory(long userId, int size, DataSource dataSource) {

        QueryWrapper<SearchHistoryPO> queryWrapper = new QueryWrapper<>();

        queryWrapper.select(SearchHistoryPO.CamelFields.content, SearchHistoryPO.CamelFields.dataSource)
                .eq(SearchHistoryPO.CamelFields.userId, userId)
                .eq(SearchHistoryPO.CamelFields.dataSource, dataSource.getValue())
                .orderByDesc(SearchHistoryPO.CamelFields.searchTime);

        return loadSearchHistory(baseMapper.selectList(queryWrapper));
    }

    private List<SearchHistory> loadSearchHistory(List<SearchHistoryPO> searchHistoryPOList) {
        if(CollectionUtil.isEmpty(searchHistoryPOList))
            return null;

        return searchHistoryPOList.stream().map(historyStruct::POtoDO).collect(Collectors.toList());
    }

}
