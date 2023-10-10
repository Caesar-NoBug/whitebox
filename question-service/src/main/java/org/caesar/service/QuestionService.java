package org.caesar.service;

import org.caesar.common.Response;
import org.caesar.common.model.dto.response.question.SubmitCodeResponse;
import org.caesar.common.model.dto.request.question.AddQuestionRequest;
import org.caesar.common.model.dto.request.question.SubmitCodeRequest;
import org.caesar.common.model.dto.request.question.UpdateQuestionRequest;
import org.caesar.model.dao.QuestionDO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author caesar
 * @description 针对表【question】的数据库操作Service
 * @createDate 2023-08-30 10:04:07
 */
public interface QuestionService extends IService<QuestionDO> {
    Response<Void> addQuestion(AddQuestionRequest request);

    Response<Void> deleteQuestion(Long qId);

    Response<Void> updateQuestion(UpdateQuestionRequest request);

    SubmitCodeResponse submitCode(SubmitCodeRequest request);
}

