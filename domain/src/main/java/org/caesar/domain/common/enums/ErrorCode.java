package org.caesar.domain.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ErrorCode {
    //TODO: 根据标准的http状态码改一下

    // 请求成功
    SUCCESS(200, "Request Success"),
    // 请求参数错误
    INVALID_ARGS_ERROR(400, "Invalid Argument"),
    // 未认证
    NOT_AUTHENTICATED_ERROR(401, "Unauthenticated User"),
    // 未授权
    NOT_AUTHORIZED_ERROR(402, "Unauthorized"),
    // 请求过多
    TOO_MUCH_REQUEST_ERROR(429, "To Many Request"),
    // 请求未找到资源
    NOT_FIND_ERROR(404, "Resource Could Not Found"),
    // 请求资源已存在
    ALREADY_EXIST_ERROR(405, "Data Already Exist"),
    // 请求重复
    DUPLICATE_REQUEST(406, "Duplicate Request"),
    // 请求正在处理中
    REQUEST_PROCESSING_ERROR(407, "Request Is Been Processing"),
    // 系统错误
    SYSTEM_ERROR(500, "Unexpected System Error"),
    // 服务不可用
    SERVICE_UNAVAILABLE_ERROR(501, "Service Unavailable");

    // 错误码
    private final int code;

    // 错误信息
    private final String message;

    public static ErrorCode of(int code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }

    public boolean isFatal() {
        return code >= 500;
    }

}
