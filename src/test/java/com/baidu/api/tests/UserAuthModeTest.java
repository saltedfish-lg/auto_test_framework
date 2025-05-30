package com.baidu.api.tests;

import com.baidu.api.base.BaseApiTest;
import com.baidu.api.enums.AuthType;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserAuthModeTest extends BaseApiTest {

    @Test(dataProvider = "authProvider")
    @Description("接口 /users 不同鉴权方式响应验证")
    public void testUsersWithVariousAuth(AuthType authType, int expectedStatus) {
        Response response = getRequestByAuth(authType)
                .basePath("/users")
                .get()
                .then()
                .extract().response();

        logResponse(authType, response);
        assertThat(response.statusCode()).isEqualTo(expectedStatus);
    }

    @Step("📤 鉴权方式: {0}，返回状态: {1}")
    public void logResponse(AuthType authType, Response response) {
        String body = response.asPrettyString();
        io.qameta.allure.Allure.addAttachment("返回内容 - " + authType.name(), body);
    }

    @DataProvider(name = "authProvider")
    public Object[][] authProvider() {
        return new Object[][]{
                {AuthType.BEARER, 200},
                {AuthType.COOKIE, 200},
                {AuthType.BASIC, 200},
                {AuthType.NONE, 401} // or 403 based on backend
        };
    }
}
