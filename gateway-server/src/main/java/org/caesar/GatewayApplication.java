package org.caesar;

import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.caesar.util.IPKeyResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@ComponentScan
@SpringBootConfiguration
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableFeignClients
@Import(RocketMQAutoConfiguration.class)
public class GatewayApplication {
    //TODO: 测试跨域
    //TODO: 添加统一缓存机制
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    private final String[] SERVICES = new String[]{
            "/user-service", "/executor-service", "/question-service", "/search-service"
    };

    private final String SERVICE_PATH = "%s/**";

    private final String SERVICE_URI = "lb:/%s";



    /*@Autowired
    @Qualifier("ipRateLimiterFactory")
    private RequestRateLimiterGatewayFilterFactory ipRateLimiterFactory;*/

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {

        RouteLocatorBuilder.Builder routes = builder.routes();
        for (int i = 0; i < SERVICES.length; i++) {
            String service = SERVICES[i];
            routes
                    .route(
                            r -> r.path(String.format(SERVICE_PATH, service))
                                    .filters(f -> f
                                                    .rewritePath(
                                                            service, ""
                                                    )/*.filter(
                                                            ipRateLimiterFactory.apply(c -> {})
                                                    )*/
                                            /*.requestRateLimiter(
                                                c -> c.setKeyResolver(new IPKeyResolver())
                                                        .setRateLimiter(redisRateLimiter)
                                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)
                                            )*/
                                    )
                                    .uri(String.format(SERVICE_URI, service))
                    );
        }

        return routes.build();
    }


}
