package org.caesar.common.util;


import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.UUID;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.caesar.common.exception.BusinessException;
import org.caesar.common.vo.Expression;
import org.caesar.common.vo.Captcha;
import org.caesar.domain.constant.StrConstant;
import org.caesar.domain.constant.enums.ErrorCode;
import org.springframework.util.FastByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Properties;

/**
 * 人机校验工具类
 */
public class CaptchaUtil {

    private static final String DEFAULT_CHAR_COUNT = "5";

    public static final String DEFAULT_CHAR_SPACE = "7";

    public static Captcha genValidationCode(int width, int height) {

        DefaultKaptcha kaptcha = new DefaultKaptcha();

        kaptcha.setConfig(new Config(defaultProperties(width, height)));

        // 验证码结果
        String result;
        //验证码展示的内容
        String show;

        if(Math.random() < 0.5) {
            result = show = StrUtil.getRandStr(6);
        }
        else {
            Expression exp = ExpressionUtil.genExpression(4, 10);
            result = String.valueOf(exp.getResult());
            show = exp.getExpression();
        }

        BufferedImage image = kaptcha.createImage(show);

        FastByteArrayOutputStream os = new FastByteArrayOutputStream();

        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成验证码失败");
        }

        String id = UUID.fastUUID().toString();

        return new Captcha(id, Base64.encode(os.toByteArray()), result);
    }

    public static void main(String[] args) {
        Captcha captcha = genValidationCode(300, 200);
        System.out.println(captcha.getImage());
        System.out.println(captcha.getResult());
    }

    private static Properties defaultProperties(int width, int height) {
        Properties properties = new Properties();
        // 设置图片无边框
        properties.setProperty("kaptcha.border", "no");
        // 背景颜色渐变开始，这里设置的是rgb值156,156,156
        properties.put("kaptcha.background.clear.from", ColorUtil.genBasicColors());
        // 背景颜色渐变结束，这里设置以白色结束
        properties.put("kaptcha.background.clear.to", "white");
        // 字体颜色，这里设置为黑色
        properties.put("kaptcha.textproducer.font.color", ColorUtil.genColors());
        // 文字间隔，单位为px
        properties.put("kaptcha.textproducer.char.space", DEFAULT_CHAR_SPACE);
        // 干扰线颜色配置，这里设置成了idea的Darcula主题的背景色
        properties.put("kaptcha.noise.color", ColorUtil.genBasicColors());
        // 字体
        properties.put("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        // 字体大小
        properties.put("kaptcha.textProducer.font.size", "-18");
        // 图片宽度
        properties.setProperty("kaptcha.image.width", String.valueOf(width));
        // 图片高度
        properties.setProperty("kaptcha.image.height", String.valueOf(height));
        // 从哪些字符中产生
        properties.setProperty("kaptcha.textproducer.char.string", StrConstant.DEFAULT_CHAR_STRING);
        // 字符个数
        properties.setProperty("kaptcha.textproducer.char.length", DEFAULT_CHAR_COUNT);

        return properties;
    }

    private static String genColors() {
        int r = (int) (Math.random() * 255);
        int g = (int) (Math.random() * 255);
        int b = (int) (Math.random() * 255);
        return r + "," + g + "," + b;
    }

    private static String genColor() {
        return String.valueOf((int) (Math.random() * 255));
    }
}


