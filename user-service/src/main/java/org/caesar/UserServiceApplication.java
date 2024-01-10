package org.caesar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

//负责提供用户相关服务及认证鉴权服务
@EnableDiscoveryClient
@SpringBootApplication
@EnableAsync
//TODO: 把mybatis里in的写法优化成join on的写法
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
    //TODO: mq等服务迁移到云服务器中
    //TODO: 建一批索引
}
