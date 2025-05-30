package com.baidu.api.enums;

public enum AuthType {
    BEARER,
    COOKIE,
    BASIC,
    OAUTH2,
    NONE;

    public static AuthType fromString(String raw) {
        if (raw == null) return NONE;
        return switch (raw.trim().toLowerCase()) {
            case "bearer" -> BEARER;
            case "oauth2" -> OAUTH2;
            case "cookie" -> COOKIE;
            case "basic" -> BASIC;
            case "none" -> NONE;
            default -> NONE;
        };
    }
}
