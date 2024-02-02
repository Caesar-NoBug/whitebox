package org.caesar.user.captcha;

import org.caesar.user.captcha.generator.CaptchaGenerator;
import org.caesar.user.captcha.vo.Captcha;
import org.caesar.user.captcha.vo.CaptchaChecker;
import org.caesar.user.captcha.vo.CaptchaType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

//TODO: 前端验证失败3次后应该刷新验证码
@Component
public class CaptchaManager implements ApplicationContextAware {

    private final Map<CaptchaType, CaptchaGenerator> generatorMap = new HashMap<>();

    // 随机生成验证码
    public Captcha genRandCaptcha(int width, int height) {
        Random random = new Random();
        CaptchaType type = CaptchaType.values()[random.nextInt(CaptchaType.values().length)];
        return genCaptcha(type, width, height);
    }

    /**
     * @param type   验证码类型
     * @param width  图片宽度
     * @param height 图片高度
     */
    public Captcha genCaptcha(CaptchaType type, int width, int height) {
        return generatorMap.get(type).genCaptcha(width, height);
    }

    public CaptchaChecker genCaptchaChecker(Captcha captcha) {
        return new CaptchaChecker(captcha.getType(), captcha.getAnswer());
    }

    /**
     * @param result  用户输入的答案
     * @param checker 服务端存储的验证码校验器
     * @return 是否验证通过
     */
    public boolean validate(String result, CaptchaChecker checker) {
        return generatorMap.get(checker.getType()).validate(result, checker.getResult());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, CaptchaGenerator> tempMap = applicationContext.getBeansOfType(CaptchaGenerator.class);
        tempMap.values().forEach(generator -> generatorMap.put(generator.getType(), generator));
    }

}
