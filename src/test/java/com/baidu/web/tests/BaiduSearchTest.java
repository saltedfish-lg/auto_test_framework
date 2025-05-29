package com.baidu.web.tests;

import com.baidu.web.base.BaseWebTest;
import com.baidu.web.steps.BaiduSearchSteps;
import io.qameta.allure.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * 百度搜索功能测试
 * 使用 Allure 注解进行报告结构化
 */
@Epic("百度搜索项目")
@Feature("百度首页搜索功能")
public class BaiduSearchTest extends BaseWebTest {

    private BaiduSearchSteps searchSteps;

    /**
     * 初始化测试环境（浏览器 + WebDriver）
     */
    @BeforeClass
    @Override
    @Parameters({"browser", "useGrid"})
    public void setup(@Optional("chrome") String browser,
                      @Optional("true") String useGrid) {
        super.setup(browser, useGrid);  // 调用父类进行 WebDriver 初始化
        searchSteps = new BaiduSearchSteps(driver); // 初始化业务步骤封装
    }

    /**
     * 百度搜索测试用例
     * @param searchTerm 要搜索的关键词
     */
    @Test(description = "验证百度搜索功能")
    @Parameters("searchTerm")
    @Description("打开百度首页，搜索关键词，断言结果中包含该关键词")
    public void testBaiduSearch(String searchTerm) {
        searchSteps.openHomePage();
        searchSteps.enterSearchTerm(searchTerm);
        searchSteps.clickSearch();
        searchSteps.assertResults(searchTerm);
    }
}
