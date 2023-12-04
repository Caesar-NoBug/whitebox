package org.caesar.common.util;

import cn.hutool.core.codec.Base64;
import org.caesar.common.captcha.generator.SlideCaptchaGenerator;
import org.caesar.common.exception.BusinessException;
import org.caesar.domain.common.enums.ErrorCode;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ImageUtil {

    /*@Resource
    private RestTemplate restTemplate;*/
    private static RestTemplate restTemplate = new RestTemplate();

    //图片地址
    private static final String IMAGE_URL = "https://picsum.photos/%s/%s";

    //透明颜色
    public static final int[] TRANSPARENCY = new int[]{0, 0, 0};

    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    //半圆所在矩形（顺序为上下左右）
    public static final List<Rectangle> HALF_CIRCLE_RANGE = Arrays.asList(
            new Rectangle(-1, -1, 2, 1),
            new Rectangle(-1, 0, 2, 1),
            new Rectangle(-1, -1, 1, 2),
            new Rectangle(0, -1, 1, 2)
    );

    public static String imageToBase64(BufferedImage image) {

        FastByteArrayOutputStream os = new FastByteArrayOutputStream();

        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "转换图片失败（BufferedImage to Base64）");
        }

        return Base64.encode(os.toByteArray());
    }

    //把原图上的圆剪切到目标图上
    public static void cutCircle(Point sourceCenter, int offsetX, int offsetY, int radius, WritableRaster source, WritableRaster dest) {

        //半径的平方
        int distance = radius * radius;

        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (i * i + j * j <= distance) {
                    int x = sourceCenter.x + i;
                    int y = sourceCenter.y + j;
                    cutPixel(x, y, x + offsetX, y + offsetY, source, dest);
                }
            }
        }
    }

    //把原图上的半圆剪切到目标图上
    public static void cutHalfCircle(Point sourceCenter, int offsetX, int offsetY, int radius, int direction, WritableRaster source, WritableRaster dest) {

        //半径的平方
        int distance = radius * radius;

        Rectangle range = HALF_CIRCLE_RANGE.get(direction);

        for (int i = range.x * radius; i <= (range.x + range.width) * radius; i++) {
            for (int j = range.y * radius; j <= (range.y + range.height) * radius; j++) {
                if (i * i + j * j <= distance) {
                    int x = sourceCenter.x + i;
                    int y = sourceCenter.y + j;
                    cutPixel(x, y, x + offsetX, y + offsetY, source, dest);
                }
            }
        }

    }

    //把原图上的点剪切到目标图上
    public static void cutPixel(int sourceX, int sourceY, int destX, int destY, WritableRaster source, WritableRaster dest) {
        int[] backPixel = source.getPixel(sourceX, sourceY, (int[]) null);
        source.setPixel(sourceX, sourceY, TRANSPARENCY);
        dest.setPixel(destX, destY, backPixel);
    }

    //把原图上的点复制到目标图上
    public static void copyPixel(int sourceX, int sourceY, int destX, int destY, WritableRaster source, WritableRaster dest) {
        int[] backPixel = source.getPixel(sourceX, sourceY, (int[]) null);
        dest.setPixel(destX, destY, backPixel);
    }

    public static void main(String[] args) {
        new SlideCaptchaGenerator().genCaptcha(300, 200);
    }

    public static BufferedImage getRandImage(int width, int height) {

        String url = String.format(IMAGE_URL, width, height);

        byte[] bytes = restTemplate.getForObject(url, byte[].class);
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

        BufferedImage image;
        try {
            image = ImageIO.read(bis);
        } catch (IOException e) {
            throw new RuntimeException(e + "获取图片失败：url");
        }

        return image;
    }
}
