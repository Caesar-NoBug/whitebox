package org.caesar.domain.user.request;

import lombok.Data;

@Data
public class CaptchaRequest {
    /**
     *   人机校验id
     */
    private String captchaId;

    /**
     *   人机校验结果
     */
    private String answer;
}
