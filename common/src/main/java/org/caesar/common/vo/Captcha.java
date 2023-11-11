package org.caesar.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Captcha {

    /**
     * 验证id
     */
    private String id;

    /**
     * 验证码图片（base64编码）
     */
    private String image;

    /**
     * 验证结果
     */
    private String result;
}
