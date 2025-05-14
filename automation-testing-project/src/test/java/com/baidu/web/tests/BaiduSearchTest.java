package com.baidu.web.tests;

import com.baidu.web.base.BaseWebTest;
import com.baidu.web.steps.BaiduSearchSteps;
import io.qameta.allure.Description;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class BaiduSearchTest extends BaseWebTest {

    private BaiduSearchSteps searchSteps;

    @BeforeClass
    @Override
    public void setup() {
        super.setup();  // 调用父类进行 WebDriver 初始化
        searchSteps = new BaiduSearchSteps(driver);
    }

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
