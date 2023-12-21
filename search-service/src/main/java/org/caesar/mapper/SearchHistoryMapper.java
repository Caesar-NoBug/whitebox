package org.caesar.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.caesar.model.po.SearchHistoryPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author caesar
* @description 针对表【search_history】的数据库操作Mapper
* @createDate 2023-12-18 16:58:23
* @Entity org.caesar.model.po.SearchHistory
*/
@Mapper
public interface SearchHistoryMapper extends BaseMapper<SearchHistoryPO> {

}




