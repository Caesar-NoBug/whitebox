package org.caesar.service;

import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.search.enums.SortField;
import org.caesar.domain.search.vo.Index;
import org.caesar.common.model.vo.PageVO;

import java.util.List;

public interface SearchService<T extends Index> {

    /**
     * @param keyword 查询关键词
     * @param from 页数
     * @param size 页大小
     * @return 查询结果
     */
    PageVO<T> search(String keyword, int from, int size);

    PageVO<T> sortSearch(String keyword, SortField field, int from, int size);

    boolean insertIndex(List<T> indices, DataSource source);

    boolean deleteIndex(List<Long> ids, DataSource source);

    DataSource getDataSource();
}
