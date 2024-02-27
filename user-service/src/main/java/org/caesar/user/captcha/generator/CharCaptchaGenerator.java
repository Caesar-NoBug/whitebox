package org.caesar.user.captcha.generator;

import cn.hutool.core.lang.UUID;
import org.caesar.common.log.LogUtil;
import org.caesar.user.captcha.vo.Captcha;
import org.caesar.user.captcha.vo.CaptchaType;
import org.caesar.common.str.StrUtil;
import org.springframework.stereotype.Component;

@Component
public class CharCaptchaGenerator extends SimpleCaptchaGenerator {

    @Override
    public Captcha genCaptcha(int width, int height) {

        String str = StrUtil.randStrCode(6);

        Captcha captcha = new Captcha();
        captcha.setId(UUID.fastUUID().toString());
        captcha.setType(CaptchaType.CHAR);
        captcha.setImage(genImageBase64(width, height, str));
        captcha.setAnswer(str);

        return captcha;
    }

    @Override
    public boolean validate(String result, String answer) {
        LogUtil.bizLog("[CAPTCHA] result: {}, answer: {}", result, answer);
        return answer.equalsIgnoreCase(result);
    }

    @Override
    public CaptchaType getType() {
        return CaptchaType.CHAR;
    }

}
