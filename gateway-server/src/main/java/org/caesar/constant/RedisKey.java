package org.caesar.constant;

public class RedisKey {
    // 幂等请求标识, 参数为 userId，requestId
    public static final String IDEMPOTENT_REQUEST_ID_KEY = "gateway:requestId:%s:%s";
}
