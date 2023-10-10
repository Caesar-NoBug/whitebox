package org.caesar.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.caesar.common.constant.enums.ErrorCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T>{

    private int code;
    private T data;
    private String msg;

    public static <T> Response<T> ok(T data){
        return new Response<T>(ErrorCode.SUCCESS.getCode(), data, "请求成功");
    }

    public static <T> Response<T> ok(T data, String msg){
        return new Response<T>(ErrorCode.SUCCESS.getCode(), data, msg);
    }

    /*public static <T> Response<T> error(T data, String msg){
        return new Response<T>(ErrorCode.ILLEGAL_PARAM_ERROR.getCode(), data, msg);
    }*/

    public static <T> Response<Void> error(ErrorCode errorCode, String msg){
        return new Response<>(errorCode.getCode(), null, msg);
    }

    public static <T> Response<Void> error(int code, String msg){
        return new Response<>(code, null, msg);
    }

    public static <T> Response<T> error(String msg){
        return new Response<>(ErrorCode.ILLEGAL_PARAM_ERROR.getCode(), null, msg);
    }

}
