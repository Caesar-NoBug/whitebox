package org.caesar.common.captcha.generator;

import cn.hutool.core.lang.UUID;
import org.caesar.common.captcha.vo.Captcha;
import org.caesar.common.captcha.vo.CaptchaType;
import org.caesar.common.captcha.vo.CharCaptcha;
import org.caesar.common.str.StrUtil;
import org.springframework.stereotype.Component;

@Component
public class CharCaptchaGenerator extends SimpleCaptchaGenerator {

    @Override
    public Captcha genCaptcha(int width, int height) {

        String str = StrUtil.genRandStr(6);

        CharCaptcha captcha = new CharCaptcha();
        captcha.setId(UUID.fastUUID().toString());
        captcha.setType(CaptchaType.CHAR);
        captcha.setImage(genImageBase64(width, height, str));
        captcha.setAnswer(str);

        return captcha;
    }

    @Override
    public boolean validate(String result, String answer) {
        return answer.equals(result);
    }

    @Override
    public CaptchaType getType() {
        return CaptchaType.CHAR;
    }
}
