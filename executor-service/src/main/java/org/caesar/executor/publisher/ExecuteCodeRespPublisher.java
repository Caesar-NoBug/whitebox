package org.caesar.executor.publisher;


import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.caesar.common.log.Logger;
import org.caesar.common.vo.MessageDTO;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ExecuteCodeRespPublisher {

    @Value("${rocketmq.producer.topic}")
    private String topic;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public ExecuteCodeRespPublisher(RocketMQTemplate template) {
        this.rocketMQTemplate = template;
    }

    @Logger("send execute code response")
    public void sendExecuteCodeRespMessage(Response<ExecuteCodeResponse> response) {
        rocketMQTemplate.convertAndSend(topic, new MessageDTO<>(response));
    }

}
