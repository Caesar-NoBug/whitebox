package org.caesar.common.util;

import org.caesar.domain.constant.Headers;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SwaggerCore {
    /**
     * 默认Docket构建器
     *
     * @param projectName    项目名称
     * @param apiBasePackage API扫描基础包
     * @param groupName      API分组名称
     * @return Docket对象
     */
    public static Docket defaultDocketBuilder(String projectName, String apiBasePackage, String groupName) {

        /*List<ResponseMessage> responseMessages = new ArrayList<>();
        responseMessages.add(new ResponseMessageBuilder().code(200).message("请求成功").responseModel(new ModelRef("ApiError")).build());
        responseMessages.add(new ResponseMessageBuilder().code(400).message("请求失败：非法参数").responseModel(new ModelRef("ApiError")).build());
        responseMessages.add(new ResponseMessageBuilder().code(401).message("请求失败：未登录").responseModel(new ModelRef("ApiError")).build());
        responseMessages.add(new ResponseMessageBuilder().code(402).message("请求失败：无权限访问").responseModel(new ModelRef("ApiError")).build());
        responseMessages.add(new ResponseMessageBuilder().code(403).message("请求失败：请求频率过高").responseModel(new ModelRef("ApiError")).build());
        responseMessages.add(new ResponseMessageBuilder().code(404).message("请求失败：找不到该资源").responseModel(new ModelRef("ApiError")).build());
        responseMessages.add(new ResponseMessageBuilder().code(405).message("请求失败：数据已存在").responseModel(new ModelRef("ApiError")).build());
        responseMessages.add(new ResponseMessageBuilder().code(406).message("请求失败：请勿重复请求").responseModel(new ModelRef("ApiError")).build());
        responseMessages.add(new ResponseMessageBuilder().code(500).message("请求失败：系统内部错误").responseModel(new ModelRef("ApiError")).build());*/

        return new Docket(DocumentationType.SWAGGER_2)
                /*.globalResponseMessage(RequestMethod.GET, responseMessages)
                .globalResponseMessage(RequestMethod.POST, responseMessages)
                .globalResponseMessage(RequestMethod.PUT, responseMessages)
                .globalResponseMessage(RequestMethod.DELETE, responseMessages)*/
                .select()
                .apis(RequestHandlerSelectors.basePackage(apiBasePackage))
                .paths(PathSelectors.any())
                .build()
                .groupName(groupName)
                .apiInfo(createApiInfo(projectName))
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 构建文档说明对象
     *
     * @param projectName 项目名称
     * @return ApiInfo对象
     */
    private static ApiInfo createApiInfo(String projectName) {

        return new ApiInfoBuilder()
                .title(projectName + " API接口服务")
                .description("用于" + projectName + "前后端交互，提供一套API说明")
                .version("1.0.0")
                .contact(new Contact("caesar", "https://github.com/Caesar-NoBug", "1460698739@qq.com"))
                .license("Apache 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0.html")
                .build();
    }

    /**
     * 构建权限协议列表
     *
     * @return 认证协议列表
     */
    private static List<SecurityScheme> securitySchemes() {
        return Collections.singletonList(
                new ApiKey("Authorization", Headers.TOKEN_HEADER, "header"));
    }

    /**
     * 构建权限上下文列表
     *
     * @return 认证上下文列表
     */
    private static List<SecurityContext> securityContexts() {
        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .build()
        );
    }

    /**
     * 构建默认认证作用域列表
     *
     * @return 认证作用域列表
     */
    private static List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(new SecurityReference("Authorization", authorizationScopes));
    }
}
