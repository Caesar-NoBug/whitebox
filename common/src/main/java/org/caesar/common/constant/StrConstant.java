package org.caesar.common.constant;

import java.nio.charset.Charset;

public class StrConstant {

    public static final String LOGIN_PAGE_PATH = "http://localhost:8081/login";
    public static final String DEFAULT_AVATAR_URL = "";
    public static final String DEFAULT_CHARSET_NAME = "UTF-8";
    public static final Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_CHARSET_NAME);
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String HEADER_FORMAT = "%s: %s";

    public static final String USER_SERVICE = "user-service";
    public static final String QUESTION_SERVICE = "question-service";
    public static final String EXECUTOR_SERVICE = "executor-service";
    public static final String SEARCH_SERVICE = "search-service";
    public static final String GATEWAY_SERVICE = "gateway-server";

    public static final String QUESTION_INDEX = "question_index";

}
