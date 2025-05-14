package com.baidu.utils;

import io.qameta.allure.Step;

public class AllureSteps {

    @Step("打开百度首页")
    public static void openHomePage() {
        // 逻辑调用
    }

    @Step("输入搜索关键词: {searchTerm}")
    public static void searchForKeyword(String searchTerm) {
        // 逻辑调用
    }
}
