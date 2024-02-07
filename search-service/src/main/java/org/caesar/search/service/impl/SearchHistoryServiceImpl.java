package org.caesar.search.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.caesar.domain.search.vo.SearchHistoryVO;
import org.caesar.search.model.MsHistoryStruct;
import org.caesar.search.model.entity.SearchHistory;
import org.caesar.search.model.po.SearchHistoryPO;
import org.caesar.search.repository.SearchHistoryRepository;
import org.caesar.search.service.SearchHistoryService;
import org.caesar.search.mapper.SearchHistoryMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author caesar
* @description 针对表【search_history】的数据库操作Service实现
* @createDate 2023-12-18 16:58:23
*/
@Service
public class SearchHistoryServiceImpl extends ServiceImpl<SearchHistoryMapper, SearchHistoryPO>
    implements SearchHistoryService{

    @Resource
    private MsHistoryStruct historyStruct;

    @Resource
    private SearchHistoryRepository searchHistoryRepo;

    @Override
    public List<SearchHistoryVO> getSearchHistory(long userId, int size) {
        return loadSearchHistoryVO(searchHistoryRepo.getSearchHistory(userId, size));
    }

    private List<SearchHistoryVO> loadSearchHistoryVO(List<SearchHistory> searchHistoryPOList) {
        return searchHistoryPOList.stream().map(historyStruct::DOtoVO).collect(Collectors.toList());
    }
}




