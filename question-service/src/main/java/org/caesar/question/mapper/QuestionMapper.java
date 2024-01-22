package org.caesar.question.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.caesar.question.model.po.QuestionPO;
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
//TODO: 添加提交数和通过数的字段
public interface QuestionMapper extends BaseMapper<QuestionPO> {

    List<QuestionPO> selectQuestionByUpdateTime(LocalDateTime afterTime);

}




