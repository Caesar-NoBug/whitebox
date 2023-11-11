package org.caesar.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.caesar.model.po.QuestionPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author caesar
* @description 针对表【question】的数据库操作Mapper
* @createDate 2023-08-30 10:04:07
* @Entity org.caesar.model.entity.Question
*/
@Mapper
public interface QuestionMapper extends BaseMapper<QuestionPO> {

    List<QuestionPO> selectQuestionByUpdateTime(LocalDateTime afterTime);

}




