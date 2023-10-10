package org.caesar.config;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQPullConsumer;
import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//TODO：Listener 移动到question service
@Configuration
public class MQConfig {

   /* @Value("${rocketmq.name-server}")
    private String nameSrvAddr;

    @Value("${rocketmq.consumer.topic}")
    private String topic;

    @Value("${rocketmq.consumer.group}")
    private String group;

    @Bean
    public MQPushConsumer mqPushConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group);
        consumer.setNamesrvAddr(nameSrvAddr);
        consumer.subscribe(topic, "");
        consumer.setMessageListener(new ExecuteMessageListener());
        consumer.start();
        return consumer;
    }*/

}
