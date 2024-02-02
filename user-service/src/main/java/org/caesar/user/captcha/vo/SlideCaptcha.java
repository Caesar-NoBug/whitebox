package org.caesar.user.captcha.vo;

import lombok.Data;

/**
 * 滑动校验
 */
@Data
public class SlideCaptcha extends Captcha{

    /**
     * 背景图片
     */
    private String background;

    /**
     * 滑块图片
     */
    private String slider;

    /**
     * 滑块纵坐标
     */
    private int y;
}
