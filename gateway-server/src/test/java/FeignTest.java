import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.caesar.gateway.GatewayApplication;
import org.caesar.common.client.UserClient;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.common.exception.ThrowUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


@SpringBootTest(classes = GatewayApplication.class)
public class FeignTest {

    @Autowired
    private UserClient userClient;

    @Autowired
    private ObjectProvider<UserClient> userClientProvider;

    @Test
    public void feignTest(){
        String jwt = "1234456";
        String path = "user-service/reset/email";

        CompletableFuture<Response<String>> future = userClientProvider.getIfAvailable().authorize(jwt, path);

        future.orTimeout(2, TimeUnit.SECONDS)
                .exceptionally(
                        e -> {
                            System.out.println("异常：" + e);
                            return null;
                        }
                ).thenApply(
                        resp -> {
                            System.out.print("响应：");
                            System.out.println(resp);
                            return null;
                        }
                );

        Object result = Mono.fromFuture(future)

                .onErrorComplete(res -> {
                    System.out.println("出错了" + res.getMessage());
                    return false;
                })
                .flatMap(
                authorizeResponse -> {

                    System.out.println("异步回调");
                    ThrowUtil.ifTrue(authorizeResponse.getCode() != ErrorCode.SUCCESS.getCode(), ErrorCode.NOT_AUTHORIZED_ERROR, "授权失败：用户无权限访问");

                    String userId = (String) authorizeResponse.getData();

                    return null;
                }
        );

       /* Response<String> authorize = userClient.authorize(jwt, path);
        System.out.println(authorize.getData());*/
        /*CompletableFuture<Response<String>> future = userClient.authorize(jwt, path);
        future.thenApply(new Function<Response<String>, Object>() {
            @Override
            public Object apply(Response<String> stringResponse) {
                System.out.println("hui diao");
                System.out.println(stringResponse);
                return null;
            }
        });*/

    }

    @Autowired
    private RocketMQTemplate template;

    @Test
    public void testProducer() {
        template.convertAndSend("execute", "ni hao");
    }

}
