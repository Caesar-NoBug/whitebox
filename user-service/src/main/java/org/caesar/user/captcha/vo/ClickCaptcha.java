package org.caesar.user.captcha.vo;

import lombok.Data;

@Data
public class ClickCaptcha extends Captcha {
    /**
     * 验证码图片
     */
    private String image;
}
