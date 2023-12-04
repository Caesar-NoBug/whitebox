package org.caesar.common.captcha.vo;

import lombok.Data;

@Data
public class MathCaptcha extends Captcha{

    /**
     * 验证码图片
     */
    private String image;

}
