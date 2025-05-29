package com.baidu.api.tests;

import com.baidu.api.base.BaseApiTest;
import com.baidu.api.model.ErrorCaseData;
import com.baidu.utils.ExcelUtils;
import com.baidu.utils.SchemaValidatorUtils;
import com.baidu.utils.ErrorAssertUtils;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

@Epic("用户接口")
@Feature("错误场景验证")
public class UserErrorCaseTest extends BaseApiTest {

    @Test(dataProvider = "errorCaseExcelProvider")
    @Story("异常响应 + Schema 校验")
    @Description("基于 Excel 驱动测试 /users 接口异常响应结构和状态码")
    @Severity(SeverityLevel.CRITICAL)
    public void testErrorCasesFromExcel(ErrorCaseData data) {

        // 🔖 Allure 元信息
        Allure.step("📎 请求路径: " + data.getPath());
        Allure.step("📎 请求方法: " + data.getMethod());
        Allure.step("📎 使用 Token: " + (data.getToken().isEmpty() ? "(空)" : data.getToken()));
        Allure.step("📎 预期状态码: " + data.getExpectedStatus());
        Allure.step("📎 Schema 文件: " + data.getSchemaFile());

        // 🌐 发送请求
        Response response = given()
                .baseUri(BASE_URL)
                .basePath(data.getPath())
                .header("Authorization", "Bearer " + data.getToken())
                .when()
                .request(data.getMethod())
                .then()
                .statusCode(data.getExpectedStatus())
                .extract().response();

        // 📥 原始响应内容输出至 Allure 报告
        Allure.addAttachment("接口响应内容", response.asPrettyString());

        // ✅ 断言 error 字段
        ErrorAssertUtils.assertErrorFieldContains(response, data.getExpectedError());

        // ✅ 校验 Schema 文件结构
        try {
            SchemaValidatorUtils.validate(response.then(), data.getSchemaFile());
        } catch (AssertionError e) {
            Allure.addAttachment("❌ Schema 校验失败堆栈", e.getMessage());
            throw e;
        }
    }

    @DataProvider(name = "errorCaseExcelProvider")
    public Object[][] errorCaseExcelProvider() {
        List<ErrorCaseData> dataList = ExcelUtils.readErrorCases("src/test/resources/data/user_error_cases.xlsx");
        return dataList.stream().map(d -> new Object[]{d}).toArray(Object[][]::new);
    }
}
