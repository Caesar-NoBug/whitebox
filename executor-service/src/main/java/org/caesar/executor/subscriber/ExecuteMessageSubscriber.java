package org.caesar.executor.subscriber;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.caesar.domain.executor.request.ExecuteCodeRequest;
import org.caesar.domain.question.request.JudgeCodeRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@RocketMQMessageListener(topic = "${rocketmq.topic}", consumerGroup = "executor")
public class ExecuteMessageSubscriber implements RocketMQListener<ExecuteCodeRequest> {

    @Override
    public void onMessage(ExecuteCodeRequest executeCodeRequest) {
        System.out.println(executeCodeRequest);
    }

}
