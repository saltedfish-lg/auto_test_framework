package com.baidu.web.steps;

import com.baidu.utils.AssertActions;
import com.baidu.utils.ElementActions;
import com.baidu.web.pages.BaiduHomePage;
import com.baidu.web.pages.SearchResultsPage;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Baidu 搜索测试步骤类（使用 ElementActions + AssertActions 工具类）
 * 封装业务操作步骤，便于 Allure 报告生成与维护
 */
public class BaiduSearchSteps {

    private final WebDriver driver;
    private final BaiduHomePage homePage;
    private final SearchResultsPage resultsPage;

    public BaiduSearchSteps(WebDriver driver) {
        this.driver = driver;
        this.homePage = new BaiduHomePage(driver);
        this.resultsPage = new SearchResultsPage(driver);
    }

    @Step("打开百度首页")
    public void openHomePage() {
        homePage.open();
    }

    @Step("输入搜索关键词：{keyword}")
    public void enterSearchTerm(String keyword) {
        homePage.enterSearch(keyword);
    }

    @Step("点击搜索按钮")
    public void clickSearch() {
        homePage.submitSearch();
    }

    @Step("验证搜索结果包含关键词：{expected}")
    public void assertResults(String expected) {
        try {
            AssertActions.assertTextContains(driver, resultsPage.getResultsLocator(), expected);
        } catch (AssertionError | Exception e) {
            saveScreenshotOnFailure();
            throw e;
        }
    }

    /**
     * 断言失败时截图，用于 Allure 报告展示
     */
    @Attachment(value = "失败截图", type = "image/png")
    public byte[] saveScreenshotOnFailure() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
