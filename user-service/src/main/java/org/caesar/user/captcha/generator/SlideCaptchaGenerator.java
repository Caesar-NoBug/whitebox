package org.caesar.user.captcha.generator;

import cn.hutool.core.lang.UUID;
import org.caesar.user.captcha.vo.Captcha;
import org.caesar.user.captcha.vo.CaptchaType;
import org.caesar.user.captcha.vo.SlideCaptcha;
import org.caesar.common.util.ImageUtil;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.List;

@Component
public class SlideCaptchaGenerator implements CaptchaGenerator {

    //允许的最小误差(默认为1px)
    private static int MIN_ACCEPT_RANGE = 1;

    //允许的最大误差(默认为30px)
    private static int MAX_ACCEPT_RANGE = 30;

    //半圆的最大个数
    private static final int COUNT = 4;

    //滑块左上角到各个圆心的距离(除以radius后的结果)
    private static final List<Point> CENTER_DISTANCE = Arrays.asList(
            new Point(3, 1),
            new Point(3, 5),
            new Point(1, 3),
            new Point(5, 3)
    );

    @Override
    public Captcha genCaptcha(int width, int height) {

        //滑块左上角坐标
        int x = (int) ((Math.random() * 0.2 + 0.4) * width);
        int y = (int) ((Math.random() * 0.6 + 0.2) * height);

        //滑块中心正方形的直径
        int size = width >> 3;

        //滑块上的拼图凸起部分的半径
        int radius = size >> 2;

        BufferedImage background = ImageUtil.getRandImage(width, height);

        BufferedImage slider = new BufferedImage(size + radius * 2, size + radius * 2, BufferedImage.TYPE_3BYTE_BGR);

        WritableRaster sliderData = slider.getRaster();
        WritableRaster backData = background.getRaster();

        //中心的正方形部分
        for (int i = radius; i < size + radius; i++) {
            for (int j = radius; j < size + radius; j++) {
                ImageUtil.cutPixel(x + i, y + j, i, j, backData, sliderData);
            }
        }

        for (int i = 0; i < COUNT; i++) {

            double random = Math.random();
            //是否在该边剪切一个半圆(1/2的概率)
            if (random > 0.5) {
                //剪切的方向：从背景到滑块
                if (random < 0.75) {
                    Point backCenter = new Point(x + CENTER_DISTANCE.get(i).x * radius, y + CENTER_DISTANCE.get(i).y * radius);
                    ImageUtil.cutHalfCircle(backCenter, -x, -y, radius, i, backData, sliderData);
                }
                //剪切的方向：从滑块到背景
                else {
                    Point sliderCenter = new Point(CENTER_DISTANCE.get(i).x * radius, CENTER_DISTANCE.get(i).y * radius);
                    //这里要取反方向,因为取的是滑块正方形内部的半圆
                    ImageUtil.cutHalfCircle(sliderCenter, x, y, radius, i ^ 1, sliderData, backData);
                }
            }

        }

        SlideCaptcha slideCaptcha = new SlideCaptcha();
        slideCaptcha.setImage(ImageUtil.imageToBase64(background));
        slideCaptcha.setSlider(ImageUtil.imageToBase64(slider));
        slideCaptcha.setY(y);
        slideCaptcha.setId(UUID.fastUUID().toString());
        slideCaptcha.setType(CaptchaType.SLIDE);
        slideCaptcha.setAnswer(String.valueOf(x));

        return slideCaptcha;
    }

    public static void main(String[] args) {
        new SlideCaptchaGenerator().genCaptcha(300, 200);
    }

    @Override
    public CaptchaType getType() {
        return CaptchaType.SLIDE;
    }

    @Override
    public boolean validate(String result, String answer) {

        int answerPosi =  Integer.parseInt(answer);
        int resultPosi = Integer.parseInt(result);

        int dist = Math.abs(answerPosi - resultPosi);

        //在误差范围内则通过
        return dist > MIN_ACCEPT_RANGE && dist < MAX_ACCEPT_RANGE;
    }

    public static void setMinAcceptRange(int minAcceptRange) {
        MIN_ACCEPT_RANGE = minAcceptRange;
    }

    public static void setMaxAcceptRange(int maxAcceptRange) {
        MAX_ACCEPT_RANGE = maxAcceptRange;
    }

}
