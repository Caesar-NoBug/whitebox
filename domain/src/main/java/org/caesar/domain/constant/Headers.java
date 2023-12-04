package org.caesar.domain.constant;

public class Headers {
    //标记请求来源
    public static final String SOURCE_HEADER = "Source";
    //用户请求授权凭证
    public static final String TOKEN_HEADER = "Token";
    //用户唯一标识
    public static final String USERID_HEADER = "User-Id";
    //幂等请求标识
    public static final String REQUEST_ID_HEADER = "Request-Id";
    //请求追踪标识
    public static final String TRACE_ID_HEADER = "Trace-Id";
}
