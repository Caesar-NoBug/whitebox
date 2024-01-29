import com.fasterxml.jackson.core.JsonProcessingException;
import org.caesar.UserServiceApplication;
import org.caesar.common.repository.CacheRepository;
import org.caesar.user.controller.AuthController;
import org.caesar.user.mapper.MenuMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@SpringBootTest(classes = UserServiceApplication.class)
public class MyTest {

    @Autowired
    private AuthController authController;

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private CacheRepository cacheRepository;

    @Test
    public void testRedis() {
        System.out.println(cacheRepository.getAndExpire("name", String.class, 999));
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


    @Test
    public void testRoles() {
        System.out.println(menuMapper.getUpdatedRole(LocalDateTime.of(2021, 1, 1, 1, 1)));
    }

}
