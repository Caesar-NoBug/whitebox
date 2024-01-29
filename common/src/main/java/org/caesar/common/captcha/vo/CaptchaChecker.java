package org.caesar.common.captcha.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

//验证码校验参数，用于校验验证码，存储在服务端
@Data
@AllArgsConstructor
public class CaptchaChecker {

    /**
     * 验证码类型
     */
    private CaptchaType type;

    /**
     * 验证结果
     */
    private String result;
}
