package org.caesar.question.repository.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.caesar.common.exception.BusinessException;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.question.response.SubmitCodeResult;
import org.caesar.question.mapper.QuestionMapper;
import org.caesar.question.mapper.QuestionSubmitResultMapper;
import org.caesar.question.model.MsQuestionStruct;
import org.caesar.question.model.po.QuestionPO;
import org.caesar.question.model.entity.Question;
import org.caesar.question.model.po.QuestionSubmitResult;
import org.caesar.question.repository.QuestionRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author caesar
 * @description 针对表【question】的数据库操作Repository实现
 * @createDate 2023-08-30 10:04:07
 */
@Repository
public class QuestionRepositoryImpl extends ServiceImpl<QuestionMapper, QuestionPO> implements QuestionRepository {

    @Resource
    private MsQuestionStruct questionStruct;

    @Resource
    private QuestionSubmitResultMapper resultMapper;

    @Override
    public boolean addQuestion(Question question) {
        return baseMapper.insert(questionStruct.DOtoPO(question)) > 0;
    }

    @Override
    public boolean deleteQuestion(long id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateQuestion(Question question) {
        return baseMapper.updateById(questionStruct.DOtoPO(question)) > 0;
    }

    @Override
    public Question getQuestionById(long id) {
        return questionStruct.POtoDO(baseMapper.selectById(id));
    }

    @Override
    public List<Question> getUpdatedQuestion(LocalDateTime afterTime) {
        List<QuestionPO> questionDOList = baseMapper.selectQuestionByUpdateTime(afterTime);
        return questionDOList.stream().map(questionStruct::POtoDO).collect(Collectors.toList());
    }

    @Override
    public SubmitCodeResult getSubmitResult(long userId, long qId, int submitId) {
        QueryWrapper<QuestionSubmitResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("question_id", qId).eq("submit_id", submitId);
        QuestionSubmitResult submitResult = resultMapper.selectOne(queryWrapper);

        if(Objects.isNull(submitResult)) return null;

        SubmitCodeResult result;

        try {
            result = JSON.parseObject(submitResult.getResult(), SubmitCodeResult.class);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Fail to parse submit result in json: ", e);
        }

        return result;
    }

    @Override
    public void addSubmitResult(long userId, long qId, int submitId, SubmitCodeResult submitCodeResult) {

        QuestionSubmitResult submitResult = new QuestionSubmitResult();
        submitResult.setUserId(userId);
        submitResult.setQuestionId(qId);
        submitResult.setSubmitId(submitId);
        submitResult.setResult(JSON.toJSONString(submitCodeResult));
        submitResult.setCreateAt(LocalDateTime.now());

        boolean insertFlag = resultMapper.insert(submitResult) > 0;

        ThrowUtil.ifFalse(insertFlag, ErrorCode.SYSTEM_ERROR, "Fail to insert the submit result to database.");
    }

}
