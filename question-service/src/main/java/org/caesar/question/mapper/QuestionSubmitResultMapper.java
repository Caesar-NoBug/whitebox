package org.caesar.question.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.caesar.question.model.po.QuestionSubmitResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author caesar
* @description 针对表【question_submit_result】的数据库操作Mapper
* @createDate 2024-01-22 09:44:00
* @Entity org.caesar.model.po.QuestionSubmitResult
*/
@Mapper
public interface QuestionSubmitResultMapper extends BaseMapper<QuestionSubmitResult> {

}




