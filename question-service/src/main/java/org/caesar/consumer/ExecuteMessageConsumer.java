package org.caesar.consumer;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.caesar.common.Response;
import org.caesar.domain.request.question.JudgeCodeRequest;
import org.caesar.domain.response.question.JudgeCodeResponse;
import org.caesar.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "execute", consumerGroup = "group")
public class ExecuteMessageConsumer implements RocketMQListener<JudgeCodeRequest> {

    @Autowired
    private QuestionService questionService;

    @Override
    public void onMessage(JudgeCodeRequest request) {
        System.out.println(request);
        Response<JudgeCodeResponse> submitCodeResponse = questionService.judgeCode(request);
    }

}
