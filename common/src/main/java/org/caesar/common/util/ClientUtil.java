package org.caesar.common.util;

import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.vo.Response;
import org.caesar.domain.common.enums.ErrorCode;

public class ClientUtil {

    /**
     * @param response 响应
     * @param message 错误信息
     * @param <T>     返回值类型
     * @return        响应数据
     */
    public static <T> T handleResponse(Response<T> response, String message) {

        ThrowUtil.ifNull(response, ErrorCode.SYSTEM_ERROR, message + ":响应为空");

        ThrowUtil.ifTrue(response.getCode() != ErrorCode.SUCCESS.getCode(),
                ErrorCode.SYSTEM_ERROR, message);

        T data = response.getData();

        ThrowUtil.ifNull(data, ErrorCode.SYSTEM_ERROR, message + ":响应数据data为空");

        return data;
    }
}
