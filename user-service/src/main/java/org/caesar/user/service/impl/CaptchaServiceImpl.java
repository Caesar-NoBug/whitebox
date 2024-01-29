package org.caesar.user.service.impl;

import com.alibaba.fastjson.JSON;
import org.caesar.common.captcha.CaptchaManager;
import org.caesar.common.captcha.vo.Captcha;
import org.caesar.common.captcha.vo.CaptchaChecker;
import org.caesar.common.exception.BusinessException;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.log.LogUtil;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.user.service.CaptchaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Resource
    private CaptchaManager captchaManager;

    // 人机校验器
    public static final String SESSION_CAPTCHA_CHECKER = "Captcha-Checker";

    public static final String SESSION_CAPTCHA_RESULT = "Captcha-Result";

    public static final int CAPTCHA_RETRY_TIME = 3;

    @Override
    public void validate(String key, String answer, HttpSession session) {

        Integer lastResult = (Integer) session.getAttribute(SESSION_CAPTCHA_RESULT);
        ThrowUtil.ifTrue(lastResult <= -CAPTCHA_RETRY_TIME, "Captcha expired, please refresh the captcha.");

        CaptchaChecker checker = JSON.parseObject(session.getAttribute(SESSION_CAPTCHA_CHECKER).toString(), CaptchaChecker.class);

        boolean validate = captchaManager.validate(answer, checker);

        if (validate) {
            session.setAttribute(SESSION_CAPTCHA_RESULT, 1);
        } else {
            session.setAttribute(SESSION_CAPTCHA_RESULT, lastResult - 1);
            LogUtil.warn(ErrorCode.INVALID_ARGS_ERROR, "Invalid captcha answer.");
            throw new BusinessException(ErrorCode.INVALID_ARGS_ERROR, "Invalid captcha answer, please input again.");
        }

    }

    @Override
    public Captcha refreshCaptcha(int width, int height, HttpSession session) {

        Captcha captcha = captchaManager.genRandCaptcha(width, height);

        CaptchaChecker captchaChecker = captchaManager.genCaptchaChecker(captcha);
        session.setAttribute(SESSION_CAPTCHA_CHECKER, JSON.toJSONString(captchaChecker));
        session.setAttribute(SESSION_CAPTCHA_RESULT, 0);

        return captcha;
    }

}
