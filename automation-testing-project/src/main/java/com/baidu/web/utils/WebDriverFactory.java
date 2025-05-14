package com.baidu.web.utils;

import com.baidu.utils.ConfigUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.URL;
import java.util.Scanner;

public class WebDriverFactory {

    public static WebDriver createDriver(String browser, boolean useGrid) {
        try {
            if (useGrid) {
                return createRemoteDriver(browser);
            } else {
                return createLocalDriver(browser);
            }
        } catch (Exception e) {
            throw new RuntimeException("WebDriver 初始化失败: " + e.getMessage(), e);
        }
    }

    private static WebDriver createRemoteDriver(String browser) throws Exception {
        String hubUrl = ConfigUtils.getProperty("grid.hub.url", "http://localhost:4444/wd/hub");
        System.out.println("🌐 使用 Selenium Grid，Hub 地址: " + hubUrl);
        System.out.println("🧪 浏览器类型: " + browser);

        switch (browser.toLowerCase()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                applyCommonChromeOptions(chromeOptions);
                return new RemoteWebDriver(new URL(hubUrl), chromeOptions);
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                applyCommonFirefoxOptions(firefoxOptions);
                return new RemoteWebDriver(new URL(hubUrl), firefoxOptions);
            default:
                throw new IllegalArgumentException("Grid 不支持的浏览器类型: " + browser);
        }
    }

    private static WebDriver createLocalDriver(String browser) {
        System.out.println("🖥️ 使用本地浏览器模式");
        System.out.println("🧪 浏览器类型: " + browser);

        switch (browser.toLowerCase()) {
            case "chrome":
                ensureChromeDriverConfigured();
                ChromeOptions chromeOptions = new ChromeOptions();
                applyCommonChromeOptions(chromeOptions);
                return new ChromeDriver(chromeOptions);
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                applyCommonFirefoxOptions(firefoxOptions);
                return new FirefoxDriver(firefoxOptions);
            default:
                throw new IllegalArgumentException("不支持的本地浏览器类型: " + browser);
        }
    }

    private static void ensureChromeDriverConfigured() {
        String driverKey = "webdriver.chrome.driver";
        if (System.getProperty(driverKey) != null) {
            System.out.println("✅ 已设置 system property webdriver.chrome.driver");
            return;
        }

        String mode = ConfigUtils.getProperty("webdriver.path.mode", "auto").toLowerCase();
        String os = System.getProperty("os.name").toLowerCase();
        String driverPath = os.contains("win") ? "drivers/chromedriver.exe" : "drivers/chromedriver";

        switch (mode) {
            case "local":
                useLocalDriver(driverPath);
                return;
            case "system":
                useSystemPathDriver();
                return;
            case "auto":
            default:
                if (new File(driverPath).exists()) {
                    useLocalDriver(driverPath);
                } else {
                    useSystemPathDriver();
                }
        }
    }

    private static void useLocalDriver(String path) {
        if (!new File(path).exists()) {
            throw new RuntimeException("❌ 未找到本地 chromedriver: " + path);
        }
        System.setProperty("webdriver.chrome.driver", path);
        System.out.println("🧩 使用 drivers/ 中的 ChromeDriver: " + path);
        validateChromeDriverVersion(path);
    }

    private static void useSystemPathDriver() {
        try {
            Process p = new ProcessBuilder("chromedriver", "--version").start();
            String version = new Scanner(p.getInputStream()).useDelimiter("\\A").next();
            p.waitFor();

            System.out.println("🔍 检测到系统 PATH 中的 chromedriver");
            System.out.println("  ➤ 版本: " + version.trim());
            validateChromeDriverVersion("chromedriver");
        } catch (Exception e) {
            throw new RuntimeException("❌ 系统 PATH 中未找到 chromedriver，请检查环境变量", e);
        }
    }

    private static void validateChromeDriverVersion(String driverPath) {
        try {
            Process p1 = new ProcessBuilder(driverPath, "--version").start();
            String driverVersion = new Scanner(p1.getInputStream()).useDelimiter("\\A").next();
            p1.waitFor();

            String browserVersion;
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                Process p2 = Runtime.getRuntime().exec(
                        "reg query \"HKEY_CURRENT_USER\\Software\\Google\\Chrome\\BLBeacon\" /v version");
                browserVersion = new Scanner(p2.getInputStream()).useDelimiter("\\A").next();
                p2.waitFor();
            } else {
                Process p2 = new ProcessBuilder("google-chrome", "--version").start();
                browserVersion = new Scanner(p2.getInputStream()).useDelimiter("\\A").next();
                p2.waitFor();
            }

            String driverMajor = driverVersion.replaceAll("\\D+", "").substring(0, 3);
            String browserMajor = browserVersion.replaceAll("\\D+", "").substring(0, 3);

            if (!driverMajor.equals(browserMajor)) {
                System.out.println("⚠️ ChromeDriver 版本 ≠ Chrome 浏览器版本");
                System.out.println("  ChromeDriver: " + driverVersion.trim());
                System.out.println("  Chrome浏览器: " + browserVersion.trim());
                System.out.println("🔁 请下载对应版本的 ChromeDriver: https://chromedriver.chromium.org/downloads");
            } else {
                System.out.println("✅ ChromeDriver 与浏览器版本匹配");
            }
        } catch (Exception e) {
            System.out.println("⚠️ 无法检测版本匹配: " + e.getMessage());
        }
    }

    private static void applyCommonChromeOptions(ChromeOptions options) {
        if (ConfigUtils.getBoolean("browser.headless", false)) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--remote-allow-origins=*");
    }

    private static void applyCommonFirefoxOptions(FirefoxOptions options) {
        if (ConfigUtils.getBoolean("browser.headless", false)) {
//            options.setHeadless(true);
            options.addArguments("--headless");
        }
    }
}
