import com.fasterxml.jackson.core.JsonProcessingException;
import org.caesar.UserServiceApplication;
import org.caesar.common.cache.RedisCacheRepository;
import org.caesar.user.controller.AuthController;
import org.caesar.user.mapper.MenuMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@SpringBootTest(classes = UserServiceApplication.class)
public class MyTest {

    @Resource
    private AuthController authController;

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private RedisCacheRepository cacheRepository;

    @Test
    public void testRedis() {
        /*System.out.println(cacheRepository.getAndExpire("name", String.class, 999))*/;
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

    @Test
    public void testCacheRefresh() throws InterruptedException {

        cacheRepository.deleteObject("name1");
        cacheRepository.deleteObject("name2");

        cacheRepository.cache("name1", 30, 90, () -> "casear");
        cacheRepository.cache("name2", 30, 90, () -> "casear2");

        cacheRepository.cache("name1", 30, 90, () -> "casear");

        int i = 0;
        while (true) {
            Thread.sleep(5 * 1000);
            i += 5 * 1000;
            cacheRepository.refreshCache();
        }

    }

}
