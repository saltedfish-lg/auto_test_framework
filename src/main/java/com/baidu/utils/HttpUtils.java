package com.baidu.utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * HTTP 工具类：用于向 Webhook 发送 JSON 消息
 */
public class HttpUtils {

    /**
     * 向指定 URL 发送 POST JSON 请求
     * @param url 请求地址
     * @param json JSON 字符串内容
     * @throws Exception 请求失败时抛出
     */
    public static void postJson(String url, String json) throws Exception {
        HttpURLConnection conn = null;
        try {
            URL target = new URL(url);
            conn = (HttpURLConnection) target.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            try (OutputStream os = conn.getOutputStream()) {
                byte[] data = json.getBytes(StandardCharsets.UTF_8);
                os.write(data, 0, data.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HTTP 请求失败，响应码: " + responseCode);
            }

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
