package org.caesar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

//负责提供AI内容生成服务
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class AIGCServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AIGCServiceApplication.class, args);
    }
}
