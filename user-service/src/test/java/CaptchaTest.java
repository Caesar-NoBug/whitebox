import org.caesar.UserServiceApplication;
import org.caesar.common.captcha.generator.SlideCaptchaGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(classes = UserServiceApplication.class)
public class CaptchaTest {

    @Resource
    private SlideCaptchaGenerator slideCaptchaGenerator;

    @Test
    void testSlider() {
        slideCaptchaGenerator.genCaptcha(300, 200);
    }
}
