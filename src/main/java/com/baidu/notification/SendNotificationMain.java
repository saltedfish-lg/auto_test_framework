package com.baidu.notification;

import com.baidu.utils.ConfigUtils;

public class SendNotificationMain {

    public static void main(String[] args) {
        System.out.println("📲 启动通知模块...");

        try {
            String platform = ConfigUtils.getProperty("notify.platform", "wechat");
            String title = ConfigUtils.getProperty("notify.title", "自动化测试通知");
            String token = ConfigUtils.getProperty("notify.token", "");
            String user = ConfigUtils.getProperty("notify.user", "");
            System.out.println("token==="+token);

            String buildStatus = System.getProperty("build.status", "UNKNOWN");
            String reportLink = System.getProperty("report.allure.link", "");
            String screenshotPath = System.getProperty("screenshot.path", "");

            StringBuilder content = new StringBuilder();
            content.append("📢 自动化测试完成\n\n");
            content.append("✅ 状态: ").append(buildStatus).append("\n");
            if (!reportLink.isEmpty()) {
                content.append("📊 报告地址: ").append(reportLink).append("\n");
            }
            if (!screenshotPath.isEmpty()) {
                content.append("🖼️ 截图路径: ").append(screenshotPath).append("\n");
            }

            NotifyMessage message = new NotifyMessage();
            message.setPlatform(platform);
            message.setTitle(title);
            message.setContent(content.toString());
            message.setToken(token);
            message.setUser(user);

            Notifier notifier = NotificationFactory.getNotifier(platform);
            notifier.send(message);

            System.out.println("✅ 通知发送成功 → 平台：" + platform);

        } catch (Exception e) {
            System.err.println("❌ 通知发送失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}