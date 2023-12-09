package org.caesar.executor.config;

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
