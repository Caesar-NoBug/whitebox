package org.caesar.user.service.impl;

import org.caesar.common.cache.CacheRepository;
import org.caesar.user.captcha.CaptchaManager;
import org.caesar.user.captcha.vo.Captcha;
import org.caesar.user.captcha.vo.CaptchaChecker;
import org.caesar.common.exception.BusinessException;
import org.caesar.common.log.LogUtil;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.user.constant.CacheKey;
import org.caesar.user.service.CaptchaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Resource
    private CaptchaManager captchaManager;

    @Resource
    private CacheRepository cacheRepo;

    // 人机校验器
    public static final String SESSION_CAPTCHA_CHECKER = "Captcha-Checker";

    public static final String SESSION_CAPTCHA_RESULT = "Captcha-Result";

    public static final int CAPTCHA_RETRY_TIME = 3;

    @Override
    public void validate(String id, String answer) {

        String checkerKey = CacheKey.CAPTCHA_CHECKER + id;
        String resultKey = CacheKey.CAPTCHA_RESULT + id;

        Integer lastResult = cacheRepo.getObject(resultKey);

        // 重试次数过多或已过期
        if(Objects.isNull(lastResult) || lastResult <= -CAPTCHA_RETRY_TIME) {
            cacheRepo.deleteObject(checkerKey);
            throw new BusinessException(ErrorCode.TOO_MUCH_REQUEST_ERROR, "Captcha expired, please refresh the captcha.");
        }

        CaptchaChecker checker = cacheRepo.getObject(checkerKey);
        boolean validate = captchaManager.validate(answer, checker);

        if (validate) {
            cacheRepo.updateObject(resultKey, 1);
            cacheRepo.deleteObject(checkerKey);
        } else {
            cacheRepo.updateObject(resultKey, lastResult - 1);
            LogUtil.warn(ErrorCode.INVALID_ARGS_ERROR, "Invalid captcha answer.");
            throw new BusinessException(ErrorCode.INVALID_ARGS_ERROR, "Invalid captcha answer, please input again.");
        }

    }

    @Override
    public boolean validated(String id) {
        Integer result = cacheRepo.getObject(CacheKey.CAPTCHA_RESULT + id);
        return Objects.nonNull(result) && result == 1;
    }

    @Override
    public Captcha refreshCaptcha(int width, int height) {

        Captcha captcha = captchaManager.genRandCaptcha(width, height);

        CaptchaChecker captchaChecker = captchaManager.genCaptchaChecker(captcha);
        cacheRepo.setObject(CacheKey.CAPTCHA_CHECKER + captcha.getId(), captchaChecker, 10, TimeUnit.MINUTES);
        cacheRepo.setObject(CacheKey.CAPTCHA_RESULT + captcha.getId(), 0, 10, TimeUnit.MINUTES);

        return captcha;
    }

}
