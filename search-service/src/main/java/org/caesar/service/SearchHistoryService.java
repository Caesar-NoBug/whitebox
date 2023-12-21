package org.caesar.service;

import org.caesar.domain.search.vo.SearchHistoryVO;
import org.caesar.model.entity.SearchHistory;
import org.caesar.model.po.SearchHistoryPO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author caesar
* @description 针对表【search_history】的数据库操作Service
* @createDate 2023-12-18 16:58:23
*/
public interface SearchHistoryService extends IService<SearchHistoryPO> {

    /**
     * @param userId 用户id
     * @param size  记录数量
     * @return      最新搜索记录
     */
    List<SearchHistoryVO> getSearchHistory(long userId, int size);
}
