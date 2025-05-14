package com.baidu.notification;

import com.baidu.utils.ConfigUtils;
import com.baidu.utils.ReportUtils;
import org.slf4j.Logger;
import com.baidu.utils.LoggerUtils;

import java.io.IOException;

public class SendNotificationMain {
    private static final Logger logger = LoggerUtils.getLogger(SendNotificationMain.class);

    public static void main(String[] args) {
        logger.info("准备发送测试报告通知...");

        // 读取配置
        boolean enabled = Boolean.parseBoolean(ConfigUtils.getProperty("notification.enabled", "false"));
        String type = ConfigUtils.getProperty("notification.type");
        String webhook = ConfigUtils.getProperty("notification.webhook");

        if (!enabled) {
            logger.info("通知功能未开启，程序退出。");
            return;
        }

        try {
            // 生成Allure报告
            generateAllureReport();

            // 发送通知
            String reportUrl = getReportUrl();
            String title = "自动化测试完成通知";
            String content = "测试执行已完成，请点击下方链接查看测试报告。";

            Notifier notifier = NotificationFactory.getNotifier(type, webhook);
            notifier.send(title, content, reportUrl);

        } catch (Exception e) {
            logger.error("发送通知失败！", e);
        }
    }

    private static void generateAllureReport() throws IOException, InterruptedException {
        String resultDir = ConfigUtils.getProperty("report.allure.resultDir");
        String outputDir = ConfigUtils.getProperty("report.allure.outputDir");

        logger.info("生成Allure测试报告...");
        ProcessBuilder builder = new ProcessBuilder("allure", "generate", resultDir, "-o", outputDir, "--clean");
        builder.inheritIO();
        Process process = builder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Allure报告生成失败！");
        }
    }

    private static String getReportUrl() {
        // 假设报告托管在某个静态服务器上，返回报告的访问URL
        // 本地测试默认访问本地文件路径
        String outputDir = ConfigUtils.getProperty("report.allure.outputDir");
        return "file://" + System.getProperty("user.dir") + "/" + outputDir + "/" + ConfigUtils.getProperty("report.allure.indexPage");
    }
}
