package org.caesar.user.constant;

import java.util.Arrays;
import java.util.List;

public class PathList {

    public static final List<String> START_WHITE_LIST = Arrays.asList("/user/login/**", "/user/sendCode/**", "/user/refreshToken", "/user/register/**", "/user/reset", "/test", "/user/authorize");

    public static final List<String> TEST_WHITE_LIST = Arrays.asList("/test/**");

}
