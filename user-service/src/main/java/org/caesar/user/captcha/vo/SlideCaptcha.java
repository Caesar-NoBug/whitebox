package org.caesar.user.captcha.vo;

import lombok.Data;

//TODO: 目前前端不支持，日后再添加到服务中
/**
 * 滑动校验
 */
@Data
public class SlideCaptcha extends Captcha{

    /**
     * 滑块图片
     */
    private String slider;

    /**
     * 滑块纵坐标
     */
    private int y;
}
