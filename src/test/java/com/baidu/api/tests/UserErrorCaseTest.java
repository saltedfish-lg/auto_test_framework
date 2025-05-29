package com.baidu.api.tests;

import com.baidu.api.base.BaseApiTest;
import com.baidu.utils.SchemaValidatorUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class UserErrorCaseTest extends BaseApiTest {

    @Test(dataProvider = "errorCaseProvider")
    @Description("验证 /users 接口错误场景的响应结构和状态码，并进行 Schema 校验")
    public void testUserEndpointErrors(int expectedStatusCode, String expectedMessage) {
        Response response = given()
                .baseUri(BASE_URL)
                .basePath("/users")
                .header("Authorization", "Bearer invalid_token")
                .when()
                .get()
                .then()
                .statusCode(expectedStatusCode)
                .extract().response();

        logErrorResponse(response, expectedMessage);

        // ✅ Schema 校验，例如 schema/users_401.json
        try {
            SchemaValidatorUtils.validate("/users", expectedStatusCode, response);
        } catch (AssertionError e) {
            // 在日志和 Allure 中展示 schema 校验失败原因
            System.err.println("❌ Schema 校验失败: " + e.getMessage());
            throw e; // 仍然抛出失败中断该用例
        }
    }

    @Step("断言错误字段内容包含: {expected}")
    public void logErrorResponse(Response response, String expected) {
        String actualError = response.jsonPath().getString("error");
        assertThat(actualError).contains(expected);
    }

    @DataProvider(name = "errorCaseProvider")
    public Object[][] errorCaseProvider() {
        return new Object[][]{
                {401, "Unauthorized"},
                {400, "Invalid"}  // 可自定义更多错误断言场景
        };
    }
}
