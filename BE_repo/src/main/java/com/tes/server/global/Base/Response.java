package com.tes.server.global.Base;

import java.util.HashMap;
import java.util.Map;

public class Response {
    public static Map success(int status, String message, String code, Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", status);
        result.put("message", message);
        result.put("code", code);

        result.put("data", data);
        return result;
    }

    public static Map fail(int status, String message, String code) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", status);
        result.put("message", message);
        result.put("code", code);

        return result;
    }
}
