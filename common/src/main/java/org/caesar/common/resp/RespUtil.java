package org.caesar.common.resp;

import org.caesar.common.exception.BusinessException;
import org.caesar.common.log.LogUtil;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.common.enums.ErrorCode;

import java.util.Objects;

public class RespUtil {
    private static final ThrowResponseHandler throwResponseHandler = new ThrowResponseHandler();
    private static final WarnResponseHandler warnResponseHandler = new WarnResponseHandler();
    private static final InfoResponseHandler infoResponseHandler = new InfoResponseHandler();

    /**
     * 处理响应数据, 若不符合预期则执行ResponseHandler.handle
     *
     * @param response 响应
     * @param message  错误信息
     * @param <T>      返回值类型
     * @return 响应数据
     */
    public static <T> T handleWithHandler(Response<T> response, String message, ResponseHandler handler) {

        handler.handle(Objects.isNull(response), ErrorCode.SYSTEM_ERROR, message + ": null response");

        ErrorCode code = ErrorCode.of(response.getCode());

        handler.handle(!ErrorCode.SUCCESS.equals(code), code,
                message + ": (Server)" + response.getMsg());

        T data = response.getData();

        handler.handle(Objects.isNull(data), code, message + ": response data is null");

        return data;
    }

    /**
     * 处理响应数据, 若不符合预期则抛出异常
     *
     * @param response 响应
     * @param message  错误信息
     * @param <T>      返回值类型
     * @return 响应数据
     */
    public static <T> T handleWithThrow(Response<T> response, String message) {
        return handleWithHandler(response, message, throwResponseHandler);
    }

    /**
     * 处理响应数据, 若不符合预期则输出警告日志
     *
     * @param response 响应
     * @param message  日志信息
     * @param <T>      返回值类型
     * @return 响应数据
     */
    public static <T> T handleWithWarn(Response<T> response, String message) {
        return handleWithHandler(response, message, warnResponseHandler);
    }

    /**
     * 处理响应数据, 若不符合预期则输出INFO日志
     *
     * @param response 响应
     * @param message  日志信息
     * @param <T>      返回值类型
     * @return 响应数据
     */
    public static <T> T handleWithInfo(Response<T> response, String message) {
        return handleWithHandler(response, message, infoResponseHandler);
    }

    private static class ThrowResponseHandler implements ResponseHandler {
        @Override
        public void handle(boolean test, ErrorCode code, String message) {
            if(test) {
                LogUtil.warn(code, message);
                throw new BusinessException(code, message);
            }
        }
    }

    private static class WarnResponseHandler implements ResponseHandler {
        @Override
        public void handle(boolean test, ErrorCode code, String message) {
            LogUtil.warn(test, code, message);
        }
    }

    private static class InfoResponseHandler implements ResponseHandler {
        @Override
        public void handle(boolean test, ErrorCode code, String message) {
            LogUtil.bizLog(test, message);
        }
    }

}
