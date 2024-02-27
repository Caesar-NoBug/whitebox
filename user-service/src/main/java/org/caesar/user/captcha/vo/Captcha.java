package org.caesar.user.captcha.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//人机校验验证码，用于前端显示验证码
public class Captcha {

    /**
     * 验证id
     */
    private String id;

    /**
     * 验证码图片
     */
    private String image;

    /**
     * 验证码类型
     */
    private CaptchaType type;

    /**
     * 验证结果
     */
    @JsonIgnore
    private String answer;
}
