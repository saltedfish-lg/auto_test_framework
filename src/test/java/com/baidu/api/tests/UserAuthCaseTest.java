package com.baidu.api.tests;

import com.baidu.api.base.BaseApiTest;
import com.baidu.api.model.AuthCaseData;
import com.baidu.utils.ExcelUtils;
import com.baidu.utils.SchemaValidatorUtils;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("用户接口")
@Feature("鉴权类型校验")
public class UserAuthCaseTest extends BaseApiTest {

    @Test(dataProvider = "authCaseProvider")
    @Story("多鉴权方式响应校验")
    @Description("从 Excel 驱动多鉴权类型的接口访问与 Schema 验证")
    @Severity(SeverityLevel.CRITICAL)
    public void testUserAuthCases(AuthCaseData data) {
        Allure.step("📎 路径: " + data.getPath());
        Allure.step("📎 鉴权方式: " + data.getAuthType());
        Allure.step("📎 请求方法: " + data.getMethod());
        Allure.step("📎 预期状态码: " + data.getExpectedStatus());
        Allure.step("📎 Schema 文件: " + data.getSchemaFile());

        // ⛳ 发起请求
        Response response = getRequestByAuth(data.getAuthType())
                .basePath(data.getPath())
                .request(data.getHttpMethod().name())
                .then()
                .statusCode(data.getExpectedStatus())
                .extract().response();

        // 📄 附加原始响应内容
        Allure.addAttachment("📨 接口响应内容", response.asPrettyString());

        // ✅ 如果配置了 error 字段断言，执行验证
        if (data.getExpectedError() != null && !data.getExpectedError().isEmpty()) {
            String actual = response.jsonPath().getString("error");
            Allure.step("断言 error 字段包含: " + data.getExpectedError());
            assertThat(actual)
                    .as("断言 error 字段应包含预期文案")
                    .contains(data.getExpectedError());
        }

        // ✅ schema 校验（存在时执行）
        if (data.getSchemaFile() != null && !data.getSchemaFile().isEmpty()) {
            try {
                SchemaValidatorUtils.validate(response.then(), data.getSchemaFile());
            } catch (AssertionError e) {
                Allure.addAttachment("❌ Schema 校验失败", e.getMessage());
                throw e;
            }
        }
    }

    @DataProvider(name = "authCaseProvider")
    public Object[][] authCaseProvider() {
        List<AuthCaseData> list = ExcelUtils.readAuthCases("src/test/resources/data/user_auth_cases.xlsx");
        return list.stream().map(d -> new Object[]{d}).toArray(Object[][]::new);
    }
}
