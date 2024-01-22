package org.caesar.question.subscriber;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.exception.ExceptionHandler;
import org.caesar.common.log.Logger;
import org.caesar.common.resp.RespUtil;
import org.caesar.common.vo.MessageDTO;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.caesar.question.service.QuestionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@RocketMQMessageListener(topic = "${rocketmq.consumer.topic}", consumerGroup = "${rocketmq.producer.group}")
public class ExecuteMessageSubscriber implements RocketMQListener<MessageDTO<Response<ExecuteCodeResponse>>> {

    @Resource
    private QuestionService questionService;

    @Resource
    private ExceptionHandler exceptionHandler;

    @Logger(value = "execute code response message")
    @Override
    public void onMessage(MessageDTO<Response<ExecuteCodeResponse>> message) {
        ExecuteCodeResponse response = RespUtil.handleWithThrow(message.getPayload(), "Error in execute code response from [EXECUTOR SERVICE].");

        Long userId = ContextHolder.getUserId();

        exceptionHandler.handleException(() -> {
            questionService.judgeCode(userId, response);
            return Response.ok();
        });
    }

}
