package org.caesar.user.service;

import org.caesar.common.captcha.vo.Captcha;

import javax.servlet.http.HttpSession;

public interface CaptchaService {
    /**
     * @param key 唯一id（当重试次数过多时禁止id对应的请求执行captcha）
     * @param result 校验结果
     * @param session 对话session
     */
    void validate(String key, String result, HttpSession session);

    Captcha refreshCaptcha(int width, int height, HttpSession session);
}
