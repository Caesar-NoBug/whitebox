package org.caesar.model;

import org.caesar.domain.search.vo.SearchHistoryVO;
import org.caesar.model.entity.SearchHistory;
import org.caesar.model.po.SearchHistoryPO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MsHistoryStruct {
    SearchHistory POtoDO(SearchHistoryPO searchHistoryPO);
    SearchHistoryPO DOtoPO(SearchHistory searchHistory);
    SearchHistoryVO DOtoVO(SearchHistory searchHistory);
}
