package com.baidu.notification;

import com.baidu.utils.LoggerUtils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.slf4j.Logger;

/**
 * 企业微信通知实现
 */
public class WeChatNotifier implements Notifier {
    private static final Logger logger = LoggerUtils.getLogger(WeChatNotifier.class);
    private final String webhookUrl;

    public WeChatNotifier(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void send(String title, String content, String reportUrl) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            String message = "{"
                    + "\"msgtype\": \"markdown\","
                    + "\"markdown\": {"
                    + "\"content\": \"" + title + "\\n" + content + "\\n[查看测试报告](" + reportUrl + ")\""
                    + "}"
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(message.getBytes("utf-8"));
            }

            logger.info("企业微信通知发送成功");
        } catch (Exception e) {
            logger.error("企业微信通知发送失败", e);
        }
    }
}
