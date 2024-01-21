package org.caesar.task;

import org.caesar.repository.QuestionRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SyncJudgeResultTask {

    @Resource
    private QuestionRepository questionRepository;


}
