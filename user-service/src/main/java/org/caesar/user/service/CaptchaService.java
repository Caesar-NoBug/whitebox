package org.caesar.user.service;

import org.caesar.user.captcha.vo.Captcha;

import javax.servlet.http.HttpSession;

public interface CaptchaService {

    void validate(String result, HttpSession session);

    boolean validated(HttpSession session);

    Captcha refreshCaptcha(int width, int height, HttpSession session);
}
