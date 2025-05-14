package com.baidu.notification;

import com.baidu.utils.LoggerUtils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.slf4j.Logger;

/**
 * 钉钉通知实现
 */
public class DingTalkNotifier implements Notifier {
    private static final Logger logger = LoggerUtils.getLogger(DingTalkNotifier.class);
    private final String webhookUrl;

    public DingTalkNotifier(String webhookUrl) {
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
                    + "\"title\":\"" + title + "\","
                    + "\"text\":\"#### " + title + " \\n " + content + " \\n [测试报告点击查看](" + reportUrl + ")\""
                    + "}"
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(message.getBytes("utf-8"));
            }

            logger.info("钉钉通知发送成功");
        } catch (Exception e) {
            logger.error("钉钉通知发送失败", e);
        }
    }
}
