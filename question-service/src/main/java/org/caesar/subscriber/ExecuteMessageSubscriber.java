package org.caesar.subscriber;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.exception.ExceptionHandler;
import org.caesar.common.vo.MessageDTO;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.caesar.service.QuestionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@RocketMQMessageListener(topic = "${rocketmq.consumer.topic}", consumerGroup = "${rocketmq.producer.group}")
public class ExecuteMessageSubscriber implements RocketMQListener<MessageDTO<ExecuteCodeResponse>> {

    @Resource
    private QuestionService questionService;

    @Resource
    private ExceptionHandler exceptionHandler;

    @Override
    public void onMessage(MessageDTO<ExecuteCodeResponse> message) {

        System.out.println(message);

        ExecuteCodeResponse response = message.getPayload();

        Long userId = ContextHolder.getUserId();

        exceptionHandler.handleException(() -> {
            questionService.judgeCode(userId, response);
            return Response.ok();
        });
    }

}
