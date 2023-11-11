package org.caesar.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.caesar.mapper.QuestionMapper;
import org.caesar.model.QuestionPOMapper;
import org.caesar.model.po.QuestionPO;
import org.caesar.model.entity.Question;
import org.caesar.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class QuestionRepositoryImpl implements QuestionRepository {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionPOMapper poMapper;

    @Override
    public boolean addQuestion(Question question) {
        return questionMapper.insert(poMapper.DOtoPO(question)) > 0;
    }

    @Override
    public boolean deleteQuestion(long id) {
        return questionMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateQuestion(Question question) {
        return questionMapper.updateById(poMapper.DOtoPO(question)) > 0;
    }

    @Override
    public Question getQuestionById(long id) {
        return poMapper.POtoDO(questionMapper.selectById(id));
    }

    @Override
    public List<Question> getUpdatedQuestion(LocalDateTime afterTime) {
        List<QuestionPO> questionDOList = questionMapper.selectQuestionByUpdateTime(afterTime);
        return questionDOList.stream().map(poMapper::POtoDO).toList();
    }

}
