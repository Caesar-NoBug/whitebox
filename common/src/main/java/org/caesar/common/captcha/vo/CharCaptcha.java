package org.caesar.common.captcha.vo;

import com.alibaba.fastjson.JSON;
import lombok.Data;

@Data
public class CharCaptcha extends Captcha {

    /**
     * 验证码图片
     */
    private String image;

    /**
     * @param result 用户输入的结果
     * @param answer 正确结果
     * @return 是否通过验证
     */
    public static boolean validate(String result, String answer) {
        return answer.equals(result);
    }

}
