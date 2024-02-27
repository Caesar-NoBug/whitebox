package org.caesar.user.config;

import org.caesar.common.util.SwaggerCore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class UserConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ResourceLoader resourceLoader() {
        return new DefaultResourceLoader();
    }

    @Bean
    Docket systemIndexApi(){
        return SwaggerCore.defaultDocketBuilder("接口领域模型定义","org.caesar","default");
    }
}
