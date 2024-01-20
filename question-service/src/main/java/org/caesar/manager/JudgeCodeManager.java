package org.caesar.manager;

import org.caesar.service.QuestionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class JudgeCodeManager {

    @Resource
    private QuestionService questionService;
}
