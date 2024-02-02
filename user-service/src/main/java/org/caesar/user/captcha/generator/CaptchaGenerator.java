package org.caesar.user.captcha.generator;

import org.caesar.user.captcha.vo.Captcha;
import org.caesar.user.captcha.vo.CaptchaType;

public interface CaptchaGenerator {
    Captcha genCaptcha(int width, int height);
    boolean validate(String result, String answer);
    CaptchaType getType();
}
