package org.caesar.publisher;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.caesar.domain.question.request.SubmitCodeRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ExecuteMessagePublisher {

    @Value("execute")
    private String topic;

    @Resource
    private RocketMQTemplate template;

    public ExecuteMessagePublisher(RocketMQTemplate template) {
        this.template = template;
    }

    public void sendJudgeMessage(SubmitCodeRequest request) {
        template.convertAndSend(topic, request);
    }

}
