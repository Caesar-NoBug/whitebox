package org.caesar.subscriber;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.caesar.common.vo.MessageDTO;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "${rocketmq.consumer.topic}", consumerGroup = "${rocketmq.producer.group}")
public class ExecuteMessageSubscriber implements RocketMQListener<MessageDTO<ExecuteCodeResponse>> {

    @Override
    public void onMessage(MessageDTO<ExecuteCodeResponse> message) {

        System.out.println(message);

        ExecuteCodeResponse response = message.getPayload();
        //Response<JudgeCodeResponse> submitCodeResponse = questionService.judgeCode(request);
    }

}
