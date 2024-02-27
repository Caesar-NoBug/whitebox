package org.caesar.user.captcha.generator;

import cn.hutool.core.lang.UUID;
import org.caesar.user.captcha.vo.Captcha;
import org.caesar.user.captcha.vo.CaptchaType;
import org.caesar.common.util.ExpressionUtil;
import org.caesar.common.vo.Expression;
import org.springframework.stereotype.Component;

@Component
public class MathCaptchaGenerator extends SimpleCaptchaGenerator {

    @Override
    public Captcha genCaptcha(int width, int height) {

        Expression exp = ExpressionUtil.genExpression(4, 10);

        // 验证码结果
        String result = String.valueOf(exp.getResult());

        //验证码展示的内容
        String show = exp.getExpression();

        Captcha captcha = new Captcha();
        captcha.setId(UUID.fastUUID().toString());
        captcha.setType(CaptchaType.MATH);
        captcha.setImage(genImageBase64(width, height, show));
        captcha.setAnswer(result);

        return captcha;
    }

    @Override
    public boolean validate(String result, String answer) {
        return answer.equals(result);
    }

    @Override
    public CaptchaType getType() {
        return CaptchaType.MATH;
    }
}
