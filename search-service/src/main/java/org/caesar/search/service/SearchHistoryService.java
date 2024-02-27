package org.caesar.search.service;

import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.search.vo.SearchHistoryVO;
import org.caesar.search.model.po.SearchHistoryPO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author caesar
* @description 针对表【search_history】的数据库操作Service
* @createDate 2023-12-18 16:58:23
*/
public interface SearchHistoryService extends IService<SearchHistoryPO> {

    /**
     * @param userId     用户id
     * @param size       记录数量
     * @param dataSource
     * @return 最新搜索记录
     */
    List<SearchHistoryVO> getSearchHistory(long userId, int size, DataSource dataSource);
}
