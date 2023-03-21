package com.caesar.model.vo;

import com.caesar.constant.enums.Code;

import java.util.HashMap;
import java.util.Map;

public class Response {
    private Code code;
    private Map<String, Object> datas;
    private String msg;

    public Response(String name, Object data) {
        Map<String, Object> datas = new HashMap<>();
        datas.put(name, data);
        this.datas = datas;
        this.code = Code.OK;
        this.msg = "request fine";
    }

    public Response(Code code, Map datas, String msg) {
        this.code = code;
        this.datas = datas;
        this.msg = msg;
    }
}
