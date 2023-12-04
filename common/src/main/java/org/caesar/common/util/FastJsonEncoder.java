package org.caesar.common.util;

import com.alibaba.fastjson.JSON;
import feign.RequestTemplate;
import feign.codec.Encoder;

import java.lang.reflect.Type;


public class FastJsonEncoder implements Encoder {

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) {
        String jsonString = JSON.toJSONString(object);
        template.body(jsonString);
    }

}