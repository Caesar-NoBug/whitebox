package org.caesar.common.captcha.generator;

import org.caesar.common.captcha.vo.Captcha;
import org.caesar.common.captcha.vo.CaptchaType;

public interface CaptchaGenerator {
    Captcha genCaptcha(int width, int height);
    boolean validate(String result, String answer);
    CaptchaType getType();
}
