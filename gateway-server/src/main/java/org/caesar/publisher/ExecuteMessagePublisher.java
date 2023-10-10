package org.caesar.publisher;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.caesar.common.model.dto.request.question.SubmitCodeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExecuteMessagePublisher {

    @Value("execute")
    private String topic;

    @Autowired
    private RocketMQTemplate template;

    public ExecuteMessagePublisher(RocketMQTemplate template) {
        this.template = template;
    }

    public void sendExecuteMessage(SubmitCodeRequest request) {
        //template.convertAndSend(topic, "sdfsdfsd");
        template.convertAndSend(topic, request);
    }

}