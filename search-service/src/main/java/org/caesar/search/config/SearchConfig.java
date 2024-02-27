package org.caesar.search.config;

import org.caesar.common.util.SwaggerCore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SearchConfig {

    @Bean
    Docket systemIndexApi(){
        return SwaggerCore.defaultDocketBuilder("接口领域模型定义","org.caesar","default");
    }

}
