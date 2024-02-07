package org.caesar.search.repository;

import org.caesar.search.model.entity.SearchHistory;

import java.util.List;

public interface SearchHistoryRepository {

    /**
     * @param userId 用户id
     * @param size  记录数量
     * @return      最新搜索记录
     */
    List<SearchHistory> getSearchHistory(long userId, int size);

}
