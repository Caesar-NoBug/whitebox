package org.caesar.common.captcha.generator;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.caesar.common.captcha.vo.Captcha;
import org.caesar.common.captcha.vo.CaptchaType;
import org.caesar.common.captcha.vo.ClickCaptcha;
import org.caesar.common.util.ColorUtil;
import org.caesar.common.util.ImageUtil;
import org.caesar.common.str.StrUtil;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Component
public class ClickCaptchaGenerator implements CaptchaGenerator {

    //字体大小
    private static int FONT_SIZE = 20;
    //汉字个数
    private static int CHAR_COUNT = 6;
    //需要点击汉字的个数
    private static int CLICK_COUNT = 4;

    //点击汉字的最小误差范围
    private static int MIN_ACCEPT_RANGE = 4;
    //点击汉字的最大误差范围
    private static int MAX_ACCEPT_RANGE = 40;

    @Override
    public Captcha genCaptcha(int width, int height) {
        //随机选择需要点击的汉字
        Random random = new Random();

        int skipCount = CHAR_COUNT - CLICK_COUNT;
        boolean[] skip = new boolean[CHAR_COUNT];

        for (int i = 0; i < skipCount; i++) {

            int index = random.nextInt(CHAR_COUNT);
            //如果已经被选过，则重新选择
            while (skip[index]) index = random.nextInt(CHAR_COUNT);

            skip[index] = true;
        }

        //获取背景图片
        BufferedImage image = ImageUtil.getRandImage(width, height);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        String str = StrUtil.genRandCNStr(CHAR_COUNT);

        graphics.setFont(new Font("宋体", Font.BOLD, FONT_SIZE));
        StringBuilder answer = new StringBuilder("[");
        int radius = FONT_SIZE >> 1;

        for (int i = 0; i < CHAR_COUNT; i++) {

            int x = (int) ((Math.random() * 0.8 + 0.1) * width);
            int y = (int) ((Math.random() * 0.8 + 0.1) * height);

            graphics.shear(random.nextDouble() * 0.1, random.nextDouble() * 0.1);
            graphics.setColor(ColorUtil.genColor());
            graphics.drawString(str.charAt(i) + "", x, y);

            if(!skip[i]) {
                answer.append("\"").append(x + radius).append(",").append(y + radius).append("\"");
                if(i != CHAR_COUNT - 1)
                    answer.append(",");
            }

        }

        answer.append("]");

        ClickCaptcha clickCaptcha = new ClickCaptcha();
        clickCaptcha.setImage(ImageUtil.imageToBase64(image));
        clickCaptcha.setId(UUID.fastUUID().toString());
        clickCaptcha.setType(CaptchaType.CLICK);
        clickCaptcha.setAnswer(answer.toString());
        
        return clickCaptcha;
    }

    public static void main(String[] args) {
        new ClickCaptchaGenerator().genCaptcha(300, 200);
    }

    @Override
    public CaptchaType getType() {
        return CaptchaType.CLICK;
    }

    @Override
    public boolean validate(String result, String answer) {

        JSONArray results = JSON.parseArray(result);
        JSONArray answers = JSON.parseArray(answer);

        for (int i = 0; i < answers.size(); i++) {
            String[] resPos = ((String) results.get(i)).split(",");
            String[] ansPos = ((String) answers.get(i)).split(",");
            int distX = Math.abs(Integer.parseInt(resPos[0]) - Integer.parseInt(ansPos[0]));
            int distY = Math.abs(Integer.parseInt(resPos[1]) - Integer.parseInt(ansPos[1]));
            if(distX < MIN_ACCEPT_RANGE || distX > MAX_ACCEPT_RANGE || distY < MIN_ACCEPT_RANGE || distY > MAX_ACCEPT_RANGE)
                return false;
        }

        return true;
    }

    public static void setMinAcceptRange(int minAcceptRange) {
        MIN_ACCEPT_RANGE = minAcceptRange;
    }

    public static void setMaxAcceptRange(int maxAcceptRange) {
        MAX_ACCEPT_RANGE = maxAcceptRange;
    }

    public static void setFontSize(int fontSize) {
        FONT_SIZE = fontSize;
    }

    public static void setCharCount(int charCount) {
        CHAR_COUNT = charCount;
    }

    public static void setClickCount(int clickCount) {
        CLICK_COUNT = clickCount;
    }

}
