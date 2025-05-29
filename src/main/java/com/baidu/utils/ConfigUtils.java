package com.baidu.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 通用配置读取工具类，可用于 Web / API / 通知模块
 */
public class ConfigUtils {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigUtils.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                System.err.println("❌ [ConfigUtils] 未找到 application.properties 文件");
            }
        } catch (IOException ex) {
            System.err.println("⚠️ [ConfigUtils] 加载配置失败: " + ex.getMessage());
        }
    }

    /**
     * 获取字符串配置项
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * 获取字符串配置项（含默认值）
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * 获取布尔值配置项
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    /**
     * 获取整数配置项
     */
    public static int getInt(String key, int defaultValue) {
        String value = getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
