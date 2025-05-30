package com.baidu.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtils {

    private static final Properties props = new Properties();

    static {
        try {
            FileInputStream in = new FileInputStream("src/main/resources/application.properties");
            props.load(in);
            in.close();
        } catch (IOException e) {
            System.err.println("⚠️ 加载配置文件失败: " + e.getMessage());
        }
    }

    /**
     * 获取配置值（默认值为空字符串）
     */
    public static String getProperty(String key) {
        return props.getProperty(key, "");
    }

    /**
     * 获取配置值（指定默认值）
     */
    public static String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    /**
     * 获取布尔配置（默认 false）
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        String val = getProperty(key);
        if (val == null || val.isEmpty()) return defaultValue;
        return val.equalsIgnoreCase("true") || val.equals("1");
    }

    /**
     * 获取整型配置（默认 -1）
     */
    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // ✅ 获取 Token
    public static String getToken() {
        return getProperty("api.auth.token", "default_token_123");
    }

    // ✅ 获取 Cookie 值
    public static String getCookie() {
        return getProperty("api.auth.cookie", "sessionId=defaultSession");
    }

    // ✅ 获取 Basic Auth 用户名
    public static String getBasicUsername() {
        return getProperty("api.auth.basic.username", "admin");
    }

    // ✅ 获取 Basic Auth 密码
    public static String getBasicPassword() {
        return getProperty("api.auth.basic.password", "admin");
    }
}
