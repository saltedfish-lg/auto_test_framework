package com.baidu.web.pages;

import com.baidu.web.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * 所有页面类的基类，封装通用的 driver + 等待方法
 */
public abstract class BasePage {

    protected final WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * 等待元素可见并返回
     */
    protected WebElement waitForVisible(By locator) {
        return WaitUtils.waitForVisible(driver, locator);
    }

    /**
     * 等待元素可点击并返回
     */
    protected WebElement waitForClickable(By locator) {
        return WaitUtils.waitForClickable(driver, locator);
    }

    /**
     * 等待元素消失
     */
    protected boolean waitForInvisibility(By locator) {
        return WaitUtils.waitForInvisibility(driver, locator);
    }

    /**
     * 等待页面完全加载
     */
    protected void waitForPageLoad() {
        WaitUtils.waitForPageLoad(driver);
    }

    /**
     * 切换到 frame
     */
    protected void switchToFrame(By locator) {
        WaitUtils.waitForAndSwitchToFrame(driver, locator);
    }

    /**
     * 切回默认 content
     */
    protected void switchToDefault() {
        WaitUtils.switchToDefaultContent(driver);
    }
}
