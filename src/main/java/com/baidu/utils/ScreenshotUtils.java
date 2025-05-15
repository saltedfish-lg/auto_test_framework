package com.baidu.utils;

import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtils {

    public static void takeScreenshot(WebDriver driver, String testClass, String testMethod) {
        if (!(driver instanceof TakesScreenshot)) return;

        boolean enabled = ConfigUtils.getBoolean("screenshot.on.fail", true);
        if (!enabled) return;

        String path = ConfigUtils.getProperty("screenshot.path", "target/screenshots");
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        String filename = String.format("%s_%s_%s.png", testClass, testMethod, timestamp);
        File dest = new File(dir, filename);

        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), dest.toPath());
            System.out.println("📸 截图已保存: " + dest.getAbsolutePath());

            // ✅ 将截图作为 Allure 附件添加
            attachScreenshotToAllure(Files.readAllBytes(dest.toPath()));
        } catch (IOException e) {
            System.out.println("⚠️ 保存或上传截图失败: " + e.getMessage());
        }
    }

    // ✅ 用于 Allure 报告的附件注解
    @Attachment(value = "失败截图", type = "image/png")
    private static byte[] attachScreenshotToAllure(byte[] screenshotData) {
        return screenshotData;
    }
}
