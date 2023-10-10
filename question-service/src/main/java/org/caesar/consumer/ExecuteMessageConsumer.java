package org.caesar.consumer;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.caesar.constant.RedisPrefix;
import org.caesar.common.model.dto.request.question.SubmitCodeRequest;
import org.caesar.common.model.dto.response.question.SubmitCodeResponse;
import org.caesar.service.QuestionService;
import org.caesar.common.util.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "execute", consumerGroup = "group")
public class ExecuteMessageConsumer implements RocketMQListener<SubmitCodeRequest> {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RedisCache redisCache;

    @Override
    public void onMessage(SubmitCodeRequest request) {
        System.out.println(request);
        SubmitCodeResponse submitCodeResponse = questionService.submitCode(request);
        redisCache.setCacheObject(RedisPrefix.SUBMIT_RESULT + request.getQId(), submitCodeResponse);
    }

}
