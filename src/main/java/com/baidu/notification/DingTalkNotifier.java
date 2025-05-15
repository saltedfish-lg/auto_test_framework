package com.baidu.notification;

import com.baidu.utils.HttpUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 钉钉通知发送器（支持纯文本，支持 @多个用户）
 */
public class DingTalkNotifier implements Notifier {

    private final String token;

    public DingTalkNotifier(String token) {
        this.token = token;
    }

    @Override
    public void send(NotifyMessage message) {
        try {
            String url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=" + token;

            // 构建 @列表
            List<String> atMobiles = message.getAtUsers();
            boolean isAtAll = atMobiles != null && atMobiles.contains("@all");

            // 构造 JSON 消息体
            String json = "{"
                    + "\"msgtype\": \"text\","
                    + "\"text\": {"
                    + "\"content\": \"" + escapeJson(message.getContent()) + "\""
                    + "},"
                    + "\"at\": {"
                    + "\"atMobiles\": [" + atMobiles.stream()
                    .map(m -> "\"" + m + "\"")
                    .collect(Collectors.joining(",")) + "],"
                    + "\"isAtAll\": " + isAtAll
                    + "}"
                    + "}";

            System.out.println("📨 发送企业微信通知: " + json);
            HttpUtils.postJson(url, json);

        } catch (Exception e) {
            System.err.println("❌ 发送企业微信通知失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String escapeJson(String input) {
        return input.replace("\"", "\\\"").replace("\n", "\\n");
    }
}
