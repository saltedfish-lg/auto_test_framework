package com.baidu.api.enums;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    HEAD,
    OPTIONS;

    public static HttpMethod fromString(String methodStr) {
        if (methodStr == null || methodStr.trim().isEmpty()) {
            return GET; // 默认
        }
        try {
            return HttpMethod.valueOf(methodStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return GET; // fallback
        }
    }
}
