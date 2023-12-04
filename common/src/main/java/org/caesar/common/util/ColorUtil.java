package org.caesar.common.util;

import java.awt.*;
import java.util.Random;

public class ColorUtil {

    private static final String[] DEFAULT_COLORS = {
            "0,0,0", "0,0,128", "0,0,255", "0,128,0", "0,128,128", "0,128,255", "0,255,0",
            "0,255,128", "0,255,255", "128,0,0", "128,0,128", "128,0,255", "128,128,0", "128,128,128",
            "128,128,255", "128,255,0", "128,255,128", "128,255,255", "255,0,0", "255,0,128", "255,0,255",
            "255,128,0", "255,128,128", "255,128,255", "255,255,0", "255,255,128", "255,255,255"
    };

    private static final int COLOR_SIZE = 256;

    //生成R,G，B形式的颜色
    public static String genBasicColors() {
        return DEFAULT_COLORS[(int) (Math.random() * DEFAULT_COLORS.length)];
    }

    //生成R,G，B形式的颜色
    public static String genColors() {
        Random random = new Random();
        return String.format("%d,%d,%d", random.nextInt(COLOR_SIZE), random.nextInt(COLOR_SIZE), random.nextInt(COLOR_SIZE));
    }

    public static String genColorStr() {
        return String.valueOf((int) (Math.random() * 255));
    }

    public static Color genColor(){
        Random random = new Random();
        return new Color(random.nextInt(COLOR_SIZE), random.nextInt(COLOR_SIZE), random.nextInt(COLOR_SIZE));
    }

}
