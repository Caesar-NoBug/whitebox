package org.caesar.domain.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.caesar.domain.common.enums.ErrorCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T>{

    public static final Response<Void> SUCCESS_RESPONSE = new Response<>(ErrorCode.SUCCESS.getCode(), null, "request success");

    private int code;
    private T data;
    private String msg;

    public Response(ErrorCode code, T data, String msg){
        this.code = code.getCode();
        this.data = data;
        this.msg = msg;
    }

    public static Response<Void> ok(){
        return SUCCESS_RESPONSE;
    }

    public static <T> Response<T> ok(T data){
        return new Response<T>(ErrorCode.SUCCESS.getCode(), data, "request success");
    }

    public static <T> Response<T> ok(T data, String msg){
        return new Response<T>(ErrorCode.SUCCESS.getCode(), data, msg);
    }

    /*public static <T> Response<T> error(T data, String msg){
        return new Response<T>(ErrorCode.ILLEGAL_PARAM_ERROR.getCode(), data, msg);
    }*/

    public static <T> Response<T> error(ErrorCode errorCode, String msg){
        return new Response<>(errorCode.getCode(), null, msg);
    }

    public static <T> Response<Void> error(int code, String msg){
        return new Response<>(code, null, msg);
    }

    public static <T> Response<T> error(String msg){
        return new Response<>(ErrorCode.INVALID_ARGS_ERROR.getCode(), null, msg);
    }

}
