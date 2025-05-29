package com.baidu.utils;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 错误响应通用断言工具类
 */
public class ErrorAssertUtils {

    private static final Logger logger = LoggerFactory.getLogger(ErrorAssertUtils.class);

    @Step("断言 error 字段包含期望文案：{expected}")
    public static void assertErrorFieldContains(Response response, String expected) {
        String actual = response.jsonPath().getString("error");
        logger.info("实际 error 字段：{}", actual);
        Allure.addAttachment("📥 响应 error 字段", actual);

        assertThat(actual)
                .as("响应字段 error 应包含预期内容")
                .contains(expected);
    }
}
