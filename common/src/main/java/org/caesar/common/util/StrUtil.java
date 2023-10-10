package org.caesar.common.util;

import org.caesar.common.constant.enums.StrFormat;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

//TODO: 写个计时工具类
public class StrUtil {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1\\d{10}$");
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    //默认允许的最大字符串长度为8192
    public static final int DEFAULT_MAX_STRING_LENGTH = 128;

    //生成随机纯数字字符串
    public static String randNumCode(int length) {

        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        while (length > 0) {
            int num = random.nextInt(10);
            sb.append(num);
            length--;
        }

        return sb.toString();
    }

    //生成仅包含数字和字母的字符串
    public static String randStrCode(int length) {
        StringBuilder sb = new StringBuilder();

        Random random = new Random();

        while (length > 0) {
            char ch = CHARACTERS.charAt(random.nextInt(CHARACTERS.length()));
            sb.append(ch);
            length--;
        }

        return sb.toString();
    }

    //判断字符串是否为空,只要有一个为空就返回true
    public static boolean isBlank(String string) {
        return Objects.isNull(string) || string.isEmpty();
    }

    //判断字符串是否合法，即非空且长度不超过预定值
    public static boolean checkString(String string) {
        return checkString(string, DEFAULT_MAX_STRING_LENGTH);
    }

    //判断字符串是否合法，即非空且长度不超过预定值
    public static boolean checkString(String string, int maxLength) {
        return !Objects.isNull(string) && !string.isEmpty() && string.length() <= maxLength;
    }

    //判断字符串是否为邮箱格式
    public static boolean isEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    //判断字符串是否为大陆手机号格式
    public static boolean isPhone(String phone) {
        return PHONE_PATTERN.matcher(phone).matches();
    }

    //获取指定长度的随机字符串
    public static String getRandStr(int length) {
        byte[] bytes = new byte[length];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static boolean checkFormat(String string, StrFormat strFormat) {
        return strFormat.getPattern().matcher(string).matches();
    }

}
