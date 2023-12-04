package org.caesar.common.str;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.regex.Pattern;

public class JSONUtil {

    public static boolean checkJSONArray(String json) {
        return JSON.parse(json) instanceof JSONArray;
    }

}
