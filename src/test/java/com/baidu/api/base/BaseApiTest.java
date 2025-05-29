package com.baidu.api.base;

import com.baidu.utils.ConfigUtils;
import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

public class BaseApiTest {

    protected String BASE_URL;
    protected String TOKEN;

    @BeforeClass(alwaysRun = true)
    @Step("初始化 API 测试上下文")
    public void initApiContext() {
        BASE_URL = ConfigUtils.getProperty("api.base.url", "http://localhost:8080/api");
        TOKEN = getToken();
    }

    @Step("构建基础请求规范")
    public RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .addHeader("Authorization", "Bearer " + TOKEN)
                .addHeader("Content-Type", "application/json")
                .build();
    }

    @Step("获取认证 Token（可改为真实鉴权逻辑）")
    protected String getToken() {
        // 可扩展为动态 token 获取，例如调用 /auth/login 接口
        return ConfigUtils.getProperty("api.token", "your-default-token");
    }
}
