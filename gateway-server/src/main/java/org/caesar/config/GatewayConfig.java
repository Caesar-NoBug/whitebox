package org.caesar.config;

import feign.codec.Decoder;
import feign.codec.Encoder;
import org.caesar.util.FastJsonDecoder;
import org.caesar.util.FastJsonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayConfig {

    //TODO: 解决无法读取配置文件的问题
    @Value("${rateLimiter.config.ip.replenishRate:1}")
    private int IP_REPLENISH_RATE;
    @Value("${rateLimiter.config.ip.burstCapacity:2}")
    private int IP_BURST_CAPACITY;
    @Value("${rateLimiter.config.ip.requestedToken:1}")
    private int IP_REQUESTED_TOKENS;

    @Value("${rateLimiter.config.uri.replenishRate:1000}")
    private int URI_REPLENISH_RATE;
    @Value("${rateLimiter.config.uri.burstCapacity:2000}")
    private int URI_BURST_CAPACITY;
    @Value("${rateLimiter.config.uri.requestedToken:1}")
    private int URI_REQUESTED_TOKENS;

    /*@Bean
    Encoder feignEncoder() {
        return new FastJsonEncoder();
    }

    @Bean
    Decoder feignDecoder() {
        return new FastJsonDecoder();
    }*/

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*"); //addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

}
