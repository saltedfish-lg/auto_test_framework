package com.baidu.web.base;

import com.baidu.utils.ConfigUtils;
import com.baidu.utils.GridHealthCheck;
import com.baidu.utils.ScreenshotUtils;
import com.baidu.web.utils.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * 通用 Web 自动化测试基类，支持 Grid 模式、fallback、截图、线程隔离等功能。
 */
@Listeners({io.qameta.allure.testng.AllureTestNg.class})
public class BaseWebTest {

    // ThreadLocal 保证 driver 实例线程安全（并发执行下不冲突）
    protected static ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();

    // 当前线程 WebDriver 引用（可由子类继承使用）
    protected WebDriver driver;

    @Parameters({"browser", "useGrid"})
    @BeforeClass(alwaysRun = true)
    public void setup(@Optional("chrome") String browser,
                      @Optional("true") String useGrid) {

        boolean useGridFlag = Boolean.parseBoolean(useGrid);
        String hubUrl = ConfigUtils.getProperty("grid.hub.url", "http://localhost:4444");

        // ✅ Step 1: Grid 健康检查
        if (useGridFlag) {
            System.out.println("🔍 检查 Selenium Grid 健康状态...");
            if (!GridHealthCheck.isGridAvailable()) {
                System.out.println("⚠️ Grid 不可用，回退为本地执行");
                useGridFlag = false;
            } else if (!GridHealthCheck.isBrowserSupported(browser)) {
                System.out.printf("⚠️ Grid 不支持浏览器 [%s]，回退为本地执行%n", browser);
                useGridFlag = false;
            } else {
                System.out.printf("✅ Grid 正常，支持浏览器 [%s]%n", browser);
            }
        }

        // ✅ Step 2: 创建 WebDriver（来自 WebDriverFactory）
        System.out.printf("🚀 准备启动 [%s] 浏览器，执行方式：%s%n",
                browser, useGridFlag ? "Selenium Grid" : "本地驱动");

        WebDriver localDriver = WebDriverFactory.createDriver(browser, useGridFlag);
        driverThread.set(localDriver);
        this.driver = localDriver;
    }

    /**
     * 测试方法失败时截图并附加到 Allure 报告
     */
    @AfterMethod(alwaysRun = true)
    public void captureScreenshotOnFailure(ITestResult result) {
        if (!result.isSuccess() && driver != null) {
            String className = result.getTestClass().getRealClass().getSimpleName();
            String methodName = result.getMethod().getMethodName();
            ScreenshotUtils.takeScreenshot(driver, className, methodName);
        }
    }

    /**
     * 测试结束后关闭浏览器
     */
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            System.out.println("🧹 正在关闭浏览器");
            driver.quit();
            driverThread.remove();
        }
    }

    /**
     * 提供 WebDriver 获取方法，供步骤类 / 页面类 / 测试类使用
     */
    public WebDriver getDriver() {
        return driverThread.get();
    }
}
