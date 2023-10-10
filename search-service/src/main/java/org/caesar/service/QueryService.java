package org.caesar.service;

import org.caesar.common.constant.enums.SortField;
import org.caesar.common.model.vo.PageResponse;

import java.util.List;

public interface QueryService<T> {

    /**
     * @param keyword 查询关键词
     * @param from 页数
     * @param size 页大小
     * @return 查询结果
     */
    PageResponse<T> query(String keyword, int from, int size);

    PageResponse<T> sortQuery(String keyword, SortField field, int from, int size);

    boolean insertIndex(List<T> indexes);

    boolean deleteIndex(List<Integer> ids);
}
