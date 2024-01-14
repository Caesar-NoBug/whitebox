package org.caesar.domain.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ErrorCode {

    //TODO: 根据标准的http状态码改一下
    SUCCESS(200, "Request Success"),
    ILLEGAL_PARAM_ERROR(400, "Invalid Request Param"),
    NOT_AUTHENTICATED_ERROR(401, "Unauthenticated"),
    NOT_AUTHORIZED_ERROR(402, "Unauthorized"),
    TOO_MUCH_REQUEST_ERROR(429, "To Many Request"),
    NOT_FIND_ERROR(404, "Could Not Find"),
    ALREADY_EXIST_ERROR(405, "Data Already Exist"),
    DUPLICATE_REQUEST(406, "Duplicate Request"),
    SYSTEM_ERROR(500, "System Error"),
    SERVICE_UNAVAILABLE_ERROR(501, "Service Unavailable");

    private final int code;

    private final String message;
}
