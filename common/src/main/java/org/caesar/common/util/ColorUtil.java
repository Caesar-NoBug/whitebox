package org.caesar.common.util;

public class ColorUtil {

    private static final String[] DEFAULT_COLORS = {
            "0,0,0", "0,0,128", "0,0,255", "0,128,0", "0,128,128", "0,128,255", "0,255,0",
            "0,255,128", "0,255,255", "128,0,0", "128,0,128", "128,0,255", "128,128,0", "128,128,128",
            "128,128,255", "128,255,0", "128,255,128", "128,255,255", "255,0,0", "255,0,128", "255,0,255",
            "255,128,0", "255,128,128", "255,128,255", "255,255,0", "255,255,128", "255,255,255"
    };

    //生成R,G，B形式的颜色
    public static String genBasicColors() {
        return DEFAULT_COLORS[(int) (Math.random() * DEFAULT_COLORS.length)];
    }

    //生成R,G，B形式的颜色
    public static String genColors() {
        return String.format("%d,%d,%d", (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
    }

    public static String genColor() {
        return String.valueOf((int) (Math.random() * 255));
    }

}
