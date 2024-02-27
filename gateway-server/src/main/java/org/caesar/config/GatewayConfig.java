package org.caesar.config;

import org.caesar.common.util.SwaggerCore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RateLimiterProperties.class)
public class GatewayConfig {

    /*@Bean
    Docket systemIndexApi() {
        return SwaggerCore.defaultDocketBuilder("接口领域模型定义", "org.caesar", "user-service");
    }
    */

}
