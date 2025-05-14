package com.baidu.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取配置文件（application.properties）的工具类
 */
// ConfigUtils.java

public class ConfigUtils {
    private static final Properties properties = new Properties();

    static {
        try (InputStream in = ClassLoader.getSystemResourceAsStream("application.properties")) {
            if (in == null) {
                throw new RuntimeException("❌ 未找到 application.properties 配置文件！");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException("❌ 加载配置失败: " + e.getMessage(), e);
        }
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static String getProperty(String key) {
        return getProperty(key, null);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public static int getInt(String key, int defaultValue) {
        String value = getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

