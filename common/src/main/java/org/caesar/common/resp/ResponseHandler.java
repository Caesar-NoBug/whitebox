package org.caesar.common.resp;

import org.caesar.domain.common.enums.ErrorCode;

public interface ResponseHandler {
    /**
     * @param test   是否需要处理
     * @param code      响应错误码
     * @param message   错误信息
     */
    void handle(boolean test, ErrorCode code, String message);
}
