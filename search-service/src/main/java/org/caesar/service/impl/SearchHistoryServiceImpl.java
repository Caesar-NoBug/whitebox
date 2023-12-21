package org.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.caesar.domain.search.vo.SearchHistoryVO;
import org.caesar.model.MsHistoryStruct;
import org.caesar.model.entity.SearchHistory;
import org.caesar.model.po.SearchHistoryPO;
import org.caesar.repository.SearchHistoryRepository;
import org.caesar.service.SearchHistoryService;
import org.caesar.mapper.SearchHistoryMapper;
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




