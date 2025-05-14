package com.baidu.utils;

import com.baidu.utils.ConfigUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Selenium Grid 健康检查工具类（增强版）
 */
public class GridHealthCheck {

    /**
     * 检查 Grid 是否可连接，并至少有 1 个空闲节点
     * @return true 表示 Grid 可用，false 表示不可用或无空闲节点
     */
    public static boolean isGridAvailable() {
        try {
            String hubUrl = ConfigUtils.getProperty("grid.hub.url", "http://localhost:4444");
            URL url = new URL(hubUrl + "/wd/hub/status");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(1500);
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.out.println("⚠️ Grid 状态接口响应码异常: " + responseCode);
                return false;
            }

            StringBuilder jsonResponse = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }
            }

            JSONObject root = new JSONObject(jsonResponse.toString());
            JSONObject value = root.getJSONObject("value");
            JSONObject ready = value.getJSONObject("ready");
            if (!value.getBoolean("ready")) {
                System.out.println("⚠️ Grid 返回 ready=false，集群不可用");
                return false;
            }

            JSONObject nodes = value.getJSONArray("nodes").getJSONObject(0);
            int total = nodes.getInt("maxSession");
            int used = nodes.getInt("sessionCount");

            if (total - used <= 0) {
                System.out.printf("⚠️ 所有 Grid 节点已满载: total=%d, used=%d%n", total, used);
                return false;
            }

            System.out.printf("✅ Grid 可用: 剩余节点=%d%n", (total - used));
            return true;

        } catch (Exception e) {
            System.out.println("❌ Grid 连接异常: " + e.getMessage());
            return false;
        }
    }

    /**
     * 检查 Grid 是否支持指定的浏览器类型
     * @param browser 浏览器名称（chrome / firefox）
     * @return true 表示支持，false 表示不支持
     */
    public static boolean isBrowserSupported(String browser) {
        try {
            String hubUrl = ConfigUtils.getProperty("grid.hub.url", "http://localhost:4444");
            URL url = new URL(hubUrl + "/wd/hub/status");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(1500);
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) return false;

            StringBuilder jsonResponse = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }
            }

            JSONObject root = new JSONObject(jsonResponse.toString());
            JSONObject value = root.getJSONObject("value");
            JSONObject nodes = value.getJSONArray("nodes").getJSONObject(0);
            String nodeBrowsers = nodes.getJSONArray("slots").toString().toLowerCase(Locale.ROOT);

            return nodeBrowsers.contains(browser.toLowerCase());

        } catch (Exception e) {
            System.out.println("❌ 检查浏览器支持失败: " + e.getMessage());
            return false;
        }
    }
}
