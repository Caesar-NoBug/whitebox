package org.caesar.service;

import org.caesar.domain.constant.enums.DataSource;
import org.caesar.domain.constant.enums.SortField;
import org.caesar.domain.vo.search.Index;
import org.caesar.common.model.vo.Page;

import java.util.List;

public interface SearchService<T extends Index> {

    /**
     * @param keyword 查询关键词
     * @param from 页数
     * @param size 页大小
     * @return 查询结果
     */
    Page<T> search(String keyword, int from, int size);

    Page<T> sortSearch(String keyword, SortField field, int from, int size);

    boolean insertIndex(List<T> indices, DataSource source);

    boolean deleteIndex(List<Long> ids, DataSource source);

    DataSource getDataSource();
}
