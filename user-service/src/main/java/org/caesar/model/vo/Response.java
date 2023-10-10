package org.caesar.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.caesar.common.constant.NumConstant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T>{

    private int code;
    private T data;
    private String msg;

    public static <T> Response<T> ok(T data){
        return new Response(NumConstant.CODE_OK, data, "请求成功");
    }

    public static <T> Response<T> ok(T data, String msg){
        return new Response(NumConstant.CODE_OK, data, msg);
    }

    public static <T> Response<T> error(T data, String msg){
        return new Response(NumConstant.CODE_ILLEGAL_PARAM, data, msg);
    }

    public static <T> Response<T> error(String msg){
        return new Response(NumConstant.CODE_ILLEGAL_PARAM, null, msg);
    }

}
