package org.caesar.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterProperties {

    private List<RateLimiterConfig> config;

    public RateLimiterConfig getUriConfig(String uri) {

        for (RateLimiterConfig rateLimiterConfig : config) {
            if(rateLimiterConfig.getUri().equals(uri))
                return rateLimiterConfig;
        }

        return null;
    }

}
