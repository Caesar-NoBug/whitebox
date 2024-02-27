package org.caesar.user.service;

import org.caesar.user.captcha.vo.Captcha;

import javax.servlet.http.HttpSession;

public interface CaptchaService {

    void validate(String id, String answer);

    boolean validated(String id);

    Captcha refreshCaptcha(int width, int height);
}
