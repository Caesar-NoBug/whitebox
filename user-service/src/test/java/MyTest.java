import com.fasterxml.jackson.core.JsonProcessingException;
import org.caesar.UserServiceApplication;
import org.caesar.controller.AuthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = UserServiceApplication.class)
public class MyTest {

    @Autowired
    private AuthController authController;

    @Test
    public void testRedis() {
        /*UserPO user = new UserPO();
        user.setUsername("dfasdf");
        user.setPassword("wer72340r");
        user.setEmail("42134@qq.com");
        userController.register(user);*/
    }

    @Test
    public void testFuture() throws JsonProcessingException {
        /*CompletableFuture<Response<String>> fu = userController.authorize("sdfsdf", "dsfsdfs");
        *//*System.out.println(fu);
        String json = JSON.toJSONString(fu);
        System.out.println(json);*/
        /*
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(fu);
        System.out.println(json);*/
    }

}
