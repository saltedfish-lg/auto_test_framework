package com.baidu.utils;

import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class AllureAttachments {

    @Attachment(value = "页面截图", type = "image/png")
    public static byte[] captureScreenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    @Attachment(value = "请求体", type = "application/json")
    public static String attachRequestBody(String body) {
        return body;
    }

    @Attachment(value = "响应体", type = "application/json")
    public static String attachResponseBody(String body) {
        return body;
    }
}
