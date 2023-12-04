package org.caesar.common.captcha.generator;


import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.caesar.common.util.ColorUtil;
import org.caesar.common.util.ImageUtil;
import org.caesar.domain.constant.StrConstant;

import java.awt.image.BufferedImage;
import java.util.Properties;

/**
 * 人机校验工具类
 */
public abstract class SimpleCaptchaGenerator implements CaptchaGenerator{

    private static final String DEFAULT_CHAR_COUNT = "5";

    public static final String DEFAULT_CHAR_SPACE = "7";

    private DefaultKaptcha genKaptcha(int width, int height) {
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

        DefaultKaptcha kaptcha = new DefaultKaptcha();

        kaptcha.setConfig(new Config(properties));

        return kaptcha;
    }


    /**
     * @param width 图片宽度
     * @param height 图片高度
     * @param content 字符串内容
     * @return Base64验证码图片
     */
    protected String genImageBase64(int width, int height, String content) {

        DefaultKaptcha kaptcha = genKaptcha(width, height);

        BufferedImage image = kaptcha.createImage(content);

        return ImageUtil.imageToBase64(image);
    }


}


