import org.caesar.QuestionApplication;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.util.DataFilter;
import org.caesar.domain.executor.enums.CodeLanguage;
import org.caesar.domain.executor.request.ExecuteCodeRequest;
import org.caesar.domain.question.request.SubmitCodeRequest;
import org.caesar.question.controller.QuestionController;
import org.caesar.domain.question.request.AddQuestionRequest;
import org.caesar.question.model.entity.Question;
import org.caesar.question.publisher.ExecuteCodePublisher;
import org.caesar.question.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(classes = QuestionApplication.class)
public class MyTest {

    @Autowired
    private QuestionController controller;

    @Autowired
    private QuestionRepository repository;

    @Resource
    private ExecuteCodePublisher publisher;

    @Test
    public void test() {
        AddQuestionRequest request = new AddQuestionRequest();
        request.setTitle("第一个题目(a + b)");
        request.setContent("这是题目的内容");
        request.setInputCase("[\"1, 2\n\", \"3, 4\n\"]");
        request.setOutputCase("[\"3\", \"7\"]");
        request.setQType(0);
        request.setTag("简单/入门/测试");
        System.out.println(controller.addQuestion(request));
        /*SubmitCodeRequest request = new SubmitCodeRequest();
        request.setId(0);
        request.setCode("import java.util.Scanner;\n" +
                "\n" +
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        Scanner sc = new Scanner(System.in);\n" +
                "        int a = sc.nextInt();\n" +
                "        int b = sc.nextInt();\n" +
                "        System.out.println(a + b);\n" +
                "    }\n" +
                "}\n");
        request.setLanguage(CodeLanguage.JAVA);
        controller.submitCode(request);*/
    }

    @Test
    public void testQuestionRepository() {
        List<Question> question = repository.getUpdatedQuestion(LocalDateTime.of(2023, 1, 1, 0, 0, 0));
        question.forEach(System.out::println);
    }

    @Test
    public void testSubmitCode() {

        ContextHolder.setUserId(0L);
        SubmitCodeRequest request = new SubmitCodeRequest();

        request.setSubmitId(1999);
        request.setCode(
                "import java.util.Scanner;\n" +
                "\n" +
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        Scanner sc = new Scanner(System.in);\n" +
                "        int a = sc.nextInt();\n" +
                "        int b = sc.nextInt();\n" +
                "        System.out.println(a + b);\n" +
                "    }\n" +
                "}\n");
        request.setLanguage(CodeLanguage.JAVA);
        request.setQuestionId(0L);
        System.out.println(controller.submitCode(request));
    }

   /* @Resource
    private DataFilter questionFilter;

    @Test
    public void testBloomFilter() {
        questionFilter.add(0);
    }*/

    @Test
    public void testMq() {

        ExecuteCodeRequest request = new ExecuteCodeRequest();
        request.setCode("fsdfsdfsd");
        request.setLanguage(CodeLanguage.PYTHON);

        publisher.sendExecuteCodeMessage(request);
    }

}
