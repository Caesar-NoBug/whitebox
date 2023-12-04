import org.caesar.AIGCServiceApplication;
import org.caesar.controller.ChatController;
import org.caesar.domain.aigc.request.AnalyseArticleRequest;
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
        AnalyseArticleRequest request = new AnalyseArticleRequest();
        request.setTitle("");
    }

}
