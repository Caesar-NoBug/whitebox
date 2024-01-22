package org.caesar.question.task;

import org.caesar.question.repository.QuestionRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SyncJudgeResultTask {

    @Resource
    private QuestionRepository questionRepository;


}
