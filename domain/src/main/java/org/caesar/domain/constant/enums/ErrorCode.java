package org.caesar.domain.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ErrorCode {

    //TODO: 根据标准的http状态码改一下
    SUCCESS(200, "请求成功"),
    ILLEGAL_PARAM_ERROR(400, "请求失败：非法参数"),
    NOT_AUTHENTICATED_ERROR(401, "请求失败：未登录"),
    NOT_AUTHORIZED_ERROR(402, "请求失败：无权限访问"),
    TOO_MUCH_REQUEST_ERROR(429, "请求失败：请求频率过高"),
    NOT_FIND_ERROR(404, "请求失败：找不到该资源"),
    ALREADY_EXIST_ERROR(405, "请求失败：数据已存在"),
    DUPLICATE_REQUEST(406, "请求失败：请勿重复请求"),
    SYSTEM_ERROR(500, "请求失败：系统内部错误"),
    SERVICE_UNAVAILABLE_ERROR(501, "请求失败：系统繁忙,请稍后重试");

    private final int code;

    private final String message;
}
