package com.baidu.utils;

import com.baidu.web.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import io.qameta.allure.Step;

/**
 * 元素操作工具类，仅封装 Web 操作，不依赖测试框架
 */
public class ElementActions {

    @Step("点击元素：{locator}")
    public static void click(WebDriver driver, By locator) {
        WebElement element = WaitUtils.waitForClickable(driver, locator);
        element.click();
    }

    @Step("向元素输入文本：{text}")
    public static void type(WebDriver driver, By locator, String text) {
        WebElement element = WaitUtils.waitForVisible(driver, locator);
        element.clear();
        element.sendKeys(text);
    }

    public static String getText(WebDriver driver, By locator) {
        WebElement element = WaitUtils.waitForVisible(driver, locator);
        return element.getText().trim();
    }

    public static boolean isElementPresent(WebDriver driver, By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
