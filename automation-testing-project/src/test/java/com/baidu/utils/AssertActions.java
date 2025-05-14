package com.baidu.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

/**
 * 断言工具类，仅用于测试断言
 */
public class AssertActions {

    public static void assertTextContains(WebDriver driver, By locator, String expected) {
        String actual = ElementActions.getText(driver, locator);
        Assert.assertTrue(actual.contains(expected),
                "断言失败：预期包含 [" + expected + "]，实际为 [" + actual + "]");
    }

    public static void assertTextEquals(WebDriver driver, By locator, String expected) {
        String actual = ElementActions.getText(driver, locator);
        Assert.assertEquals(actual, expected,
                "断言失败：文本不一致，预期为 [" + expected + "]，实际为 [" + actual + "]");
    }
}
