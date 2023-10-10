package org.caesar.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

//TODO: Producer移动到网关
@Configuration
public class MQConfig {

    @Value("${rocketmq.name-server}")
    private String nameSrvAddr;

    @Value("${rocketmq.consumer.topic}")
    private String topic;

    @Value("${rocketmq.consumer.group}")
    private String group;

    /*@Bean
    public MQPushConsumer mqPushConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group);
        consumer.setNamesrvAddr(nameSrvAddr);
        consumer.subscribe(topic, "");
        consumer.setMessageListener(new ExecuteMessageListener());
        consumer.start();
        return consumer;
    }*/

    /*@Bean
    public RocketMQListener<String> rocketMQListener() {
        return new ExecuteMessageListener();
    }*/

}
