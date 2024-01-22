package org.caesar.question.publisher;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.caesar.common.exception.BusinessException;
import org.caesar.common.vo.MessageDTO;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.executor.request.ExecuteCodeRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ExecuteCodePublisher {

    @Value("${rocketmq.producer.topic}")
    private String topic;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public ExecuteCodePublisher(RocketMQTemplate template) {
        this.rocketMQTemplate = template;
    }

    public void sendExecuteCodeMessage(ExecuteCodeRequest request) {
        try {
            rocketMQTemplate.convertAndSend(topic, new MessageDTO<>(request));
        } catch (MessagingException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Fail to send execute code message.", e);
        }
    }

}
