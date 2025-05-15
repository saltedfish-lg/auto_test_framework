package com.baidu.notification;

import com.baidu.utils.ConfigUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class SendNotificationMain {

    public static void main(String[] args) {
        try {
            // === Step 1: 生成 Allure 报告 ===
            System.out.println("📊 正在生成 Allure 报告...");
            Process process = Runtime.getRuntime().exec("mvn allure:report");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("→ " + line);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("❌ Allure 报告生成失败，退出码：" + exitCode);
            } else {
                System.out.println("✅ 报告生成完成");
            }

            // === Step 2: 读取配置 ===
            String platform = ConfigUtils.getProperty("notify.platform", "").trim().toLowerCase();
            String token = ConfigUtils.getProperty("notify.token", "").trim();
            String title = ConfigUtils.getProperty("notify.title", "自动化测试报告");
            String userStr = ConfigUtils.getProperty("notify.user", "").trim();
            String reportLink = ConfigUtils.getProperty("report.allure.indexPage", "");
            String format = ConfigUtils.getProperty("notify.format", "text");

            if (platform.isEmpty() || token.isEmpty()) {
                System.err.println("❌ 缺少通知配置：platform 或 token");
                return;
            }

            // === Step 3: 构造内容 ===
            StringBuilder content = new StringBuilder();
            content.append("【").append(title).append("】\n")
                    .append("✅ 自动化测试执行完成\n")
                    .append("📅 时间：")
                    .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");

            if (!reportLink.isEmpty()) {
                content.append("📊 报告地址：").append(reportLink).append("\n");
            }

            // === Step 4: 构造 NotifyMessage ===
            List<String> atUsers = userStr.isEmpty() ? List.of() : Arrays.asList(userStr.split(","));
            NotifyMessage message = new NotifyMessage(title, content.toString(), atUsers, format);

            // === Step 5: 发送通知 ===
            Notifier notifier = NotificationFactory.getNotifier(platform, token);
            notifier.send(message);

            System.out.println("✅ 通知发送成功 ✅");

        } catch (Exception e) {
            System.err.println("❌ 通知异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
