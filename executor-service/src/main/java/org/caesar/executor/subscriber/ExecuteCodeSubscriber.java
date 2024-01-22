package org.caesar.executor.subscriber;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.caesar.common.log.LogUtil;
import org.caesar.common.log.Logger;
import org.caesar.common.vo.MessageDTO;
import org.caesar.domain.executor.request.ExecuteCodeRequest;
import org.caesar.executor.manager.ExecutorManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@RocketMQMessageListener(topic = "${rocketmq.consumer.topic}", consumerGroup = "${rocketmq.producer.group}")
public class ExecuteCodeSubscriber implements RocketMQListener<MessageDTO<ExecuteCodeRequest>> {

    @Resource
    private ExecutorManager executorManager;

    @Override
    public void onMessage(MessageDTO<ExecuteCodeRequest> executeCodeRequest) {
        ExecuteCodeRequest request = executeCodeRequest.getPayload();
        executorManager.executeCode(request);
    }

}
