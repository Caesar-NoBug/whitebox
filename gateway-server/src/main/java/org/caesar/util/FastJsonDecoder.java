package org.caesar.util;

import com.alibaba.fastjson.JSON;
import feign.Response;
import feign.codec.Decoder;
import org.caesar.common.util.IOUtil;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

public class FastJsonDecoder implements Decoder {

    @Override
    public Object decode(Response response, Type type) throws IOException {
        String jsonString = IOUtil.readAll(response.body().asInputStream());

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            if (typeArguments.length > 0) {
                Type responseType = typeArguments[0];
                Object responseData = JSON.parseObject(jsonString, responseType);
                CompletableFuture<Object> future = new CompletableFuture<>();
                future.complete(responseData);
                return future;
            } else {
                System.out.println("无法获取 Response<String> 的类型信息");
            }
        } else {
            System.out.println("type 不是 ParameterizedType");
        }

        return JSON.parseObject(jsonString, type);
    }

}