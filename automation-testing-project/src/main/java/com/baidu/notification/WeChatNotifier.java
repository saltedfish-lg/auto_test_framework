package com.baidu.notification;

import com.baidu.utils.HttpUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 企业微信通知发送器（Webhook 模式）
 */
public class WeChatNotifier implements Notifier {

    private final String token;

    public WeChatNotifier(String token) {
        this.token = token;
    }

    @Override
    public void send(NotifyMessage message) {
        try {
            String url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=" + token;

            // 构造被 @人列表
            List<String> atUsers = message.getAtUsers();

            String mentionedListJson = "";
            if (atUsers != null && !atUsers.isEmpty()) {
                mentionedListJson = ", \"mentioned_list\": ["
                        + atUsers.stream()
                        .map(u -> "\"" + u + "\"")
                        .collect(Collectors.joining(","))
                        + "]";
            }

            // 构造 JSON 消息体（仅 text 支持）
            String json = "{"
                    + "\"msgtype\": \"text\","
                    + "\"text\": {"
                    + "\"content\": \"" + escapeJson(message.getContent()) + "\""
                    + mentionedListJson
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
