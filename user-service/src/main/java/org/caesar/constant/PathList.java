package org.caesar.constant;

import java.util.List;

public class PathList {

    public static final List<String> START_WHITE_LIST = List.of("/user/login/**", "/user/sendCode/**", "/user/refreshToken", "/user/register/**", "/user/reset", "/test", "/user/authorize");

    public static final List<String> TEST_WHITE_LIST = List.of("/test/**");

}
