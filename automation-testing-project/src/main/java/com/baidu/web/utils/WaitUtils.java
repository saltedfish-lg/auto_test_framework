package com.baidu.web.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.function.Function;

/**
 * 智能等待工具类（进阶版）
 * 封装 WebDriver 常用显式等待操作：元素、文本、iframe、alert、JS加载等
 */
public class WaitUtils {

    private static final int DEFAULT_TIMEOUT_SECONDS = 10;

    // === 元素等待 ===

    /**
     * 等待元素可见（默认超时时间）
     */
    public static WebElement waitForVisible(WebDriver driver, By locator) {
        return waitForVisible(driver, locator, DEFAULT_TIMEOUT_SECONDS);
    }
    /**
     * 等待元素可见（自定义超时）
     */
    public static WebElement waitForVisible(WebDriver driver, By locator, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * 等待元素可点击
     */
    public static WebElement waitForClickable(WebDriver driver, By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * 等待元素存在于 DOM（不要求可见）
     */
    public static WebElement waitForPresence(WebDriver driver, By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * 等待文本出现在元素中
     */
    public static boolean waitForTextInElement(WebDriver driver, By locator, String text) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                .until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    /**
     *  等待元素消失（如 loading 动画）
     */
    public static boolean waitForInvisibility(WebDriver driver, By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                .until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // === Alert 等待 ===

    public static Alert waitForAlert(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                .until(ExpectedConditions.alertIsPresent());
    }

    // === Frame 切换 ===

    public static void waitForAndSwitchToFrame(WebDriver driver, By frameLocator) {
        new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameLocator));
    }

    public static void switchToDefaultContent(WebDriver driver) {
        driver.switchTo().defaultContent();
    }

    // === JS 加载完成 ===

    public static void waitForPageLoad(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS)).until(
                webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));
    }

    // === 自定义等待（函数式接口） ===

    public static <V> V waitUntil(WebDriver driver, Function<? super WebDriver, V> condition, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)).until(condition);
    }
}
