package org.caesar.service;

import org.caesar.common.model.vo.PageVO;
import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.search.enums.SortField;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchManager {
    // TODO: 用模板方法模式重构，统一处理缓存

    PageVO<T> search(String text, int from, int size);

    PageVO<T> sortSearch(String text, SortField field, int from, int size);

    String completion(String text) {
        return false;
    }

    boolean insertIndex(List<T> indices) {
        return false;
    }

    boolean deleteIndex(List<Long> ids) {
        return false;
    }
}
