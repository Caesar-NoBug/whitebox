import org.caesar.AIGCServiceApplication;
import org.caesar.common.context.ContextHolder;
import org.caesar.controller.ChatController;
import org.caesar.domain.aigc.request.AnalyseTextRequest;
import org.caesar.domain.aigc.request.CompletionRequest;
import org.caesar.domain.aigc.response.CompletionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(classes = {AIGCServiceApplication.class})
public class ChatTest {

    @Resource
    private ChatController controller;

    @Test
    public void testCompletion() {
        CompletionRequest request = new CompletionRequest();
        request.setMemory(2);
        request.setPreset("你是一个专业的文学家");
        request.setPrompt("你好,请记住小明和小白是同一个人");
        CompletionResponse response = controller.completion(request).getData();
        System.out.println(response);
        request.setId(response.getId());
        request.setPrompt("请问小明和小白是什么关系?");
        response = controller.completion(request).getData();
        System.out.println(response);
        request.setPrompt("请为我作一首诗");
        response = controller.completion(request).getData();
        System.out.println(response);
        request.setPrompt("请问小明和小白是什么关系?");
        response = controller.completion(request).getData();
        System.out.println(response);

    }

    @Test
    public void testAnalyseArticle() {
        ContextHolder.set(ContextHolder.USER_ID, 0L);
        AnalyseTextRequest request = new AnalyseTextRequest("Spring-Boot-操作-Redis，三种方案全解析！",
                "当使用 Spring Feign 进行服务间通信时，可以按照以下步骤进行配置和使用：\n" +
                        "\n" +
                        "步骤 1: 添加依赖\n" +
                        "在项目的 `pom.xml` 文件中添加 Spring Cloud Feign 的依赖：\n" +
                        "\n" +
                        "```xml\n" +
                        "<dependency>\n" +
                        "    <groupId>org.springframework.cloud</groupId>\n" +
                        "    <artifactId>spring-cloud-starter-openfeign</artifactId>\n" +
                        "</dependency>\n" +
                        "```\n" +
                        "\n" +
                        "步骤 2: 启用 Feign\n" +
                        "在 Spring Boot 应用程序的启动类上添加 `@EnableFeignClients` 注解，来启用 Feign 客户端。\n" +
                        "\n" +
                        "```java\n" +
                        "@SpringBootApplication\n" +
                        "@EnableFeignClients\n" +
                        "public class YourApplication {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        SpringApplication.run(YourApplication.class, args);\n" +
                        "    }\n" +
                        "}\n" +
                        "```\n" +
                        "\n" +
                        "步骤 3: 创建 Feign 客户端接口\n" +
                        "创建一个接口，用于定义服务间通信的 API。这个接口上可以使用 `@FeignClient` 注解指定需要调用的服务名。\n" +
                        "\n" +
                        "```java\n" +
                        "@FeignClient(name = \"your-service-name\")\n" +
                        "public interface YourServiceClient {\n" +
                        "    @GetMapping(\"/api/your-endpoint\")\n" +
                        "    String yourApiMethod();\n" +
                        "}\n" +
                        "```\n" +
                        "\n" +
                        "步骤 4: 使用 Feign 客户端\n" +
                        "在需要调用其他服务的地方注入 Feign 客户端，并直接使用其定义的方法进行调用。\n" +
                        "\n" +
                        "```java\n" +
                        "@RestController\n" +
                        "public class YourController {\n" +
                        "    private final YourServiceClient yourServiceClient;\n" +
                        "\n" +
                        "    public YourController(YourServiceClient yourServiceClient) {\n" +
                        "        this.yourServiceClient = yourServiceClient;\n" +
                        "    }\n" +
                        "\n" +
                        "    @GetMapping(\"/your-endpoint\")\n" +
                        "    public String yourEndpoint() {\n" +
                        "        return yourServiceClient.yourApiMethod();\n" +
                        "    }\n" +
                        "}\n" +
                        "```\n" +
                        "\n" +
                        "示例代码中，我们创建了一个名为 `YourServiceClient` 的 Feign 客户端接口，用于调用名为 `your-service-name` 的服务的 `/api/your-endpoint` 接口。然后在 `YourController` 中注入 `YourServiceClient`，并在 `/your-endpoint` 路径上调用 Feign 客户端的方法。\n" +
                        "\n" +
                        "这就是一个简单的 Spring Feign 使用的教程，希望对你有帮助！如有其他问题，请随时提问。", true);
        System.out.println(controller.analyseContent(request));
    }

}
