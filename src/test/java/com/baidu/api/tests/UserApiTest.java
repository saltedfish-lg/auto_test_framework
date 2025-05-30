package com.baidu.api.tests;

import com.baidu.api.base.BaseApiTest;
import com.baidu.api.model.User;
import com.baidu.utils.SchemaValidatorUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("用户接口")
@Feature("用户创建与列表查询")
public class UserApiTest extends BaseApiTest {

    private static final String SCHEMA_USER = "user_schema.json";
    private static final String SCHEMA_LIST = "user_list_schema.json";

    @Test(dataProvider = "csvUserProvider")
    @Story("CSV 驱动创建用户")
    @Description("从 CSV 文件读取用户数据，创建用户并验证字段 + Schema 校验")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserFromCsv(User user) {
        ValidatableResponse response = createUserAndReturnResponse(user);
        validateSchema(response, SCHEMA_USER);
        validateContent(response.extract().as(User.class), user);
    }

    @Test(dataProvider = "jsonUserProvider")
    @Story("JSON 驱动创建用户")
    @Description("从 JSON 文件读取用户数据，创建用户并验证字段 + Schema 校验")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserFromJson(User user) {
        ValidatableResponse response = createUserAndReturnResponse(user);
        validateSchema(response, SCHEMA_USER);
        validateContent(response.extract().as(User.class), user);
    }

    @Test
    @Story("查询用户列表")
    @Description("验证 GET /users 接口返回结构符合预期 schema")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUserListSchemaValidation() {
        ValidatableResponse response = given()
                .auth().oauth2(getToken())
                .when()
                .get(BASE_URL + "/users")
                .then()
                .statusCode(200);

        SchemaValidatorUtils.validate(response, SCHEMA_LIST);
    }

    // ------------------------ 工具方法 ----------------------------

    @Step("发送 POST /users 创建用户并返回响应")
    public ValidatableResponse createUserAndReturnResponse(User user) {
        return given()
                .baseUri(BASE_URL)
                .basePath("/users")
                .contentType(ContentType.JSON)
                .auth().oauth2(getToken())
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(201);
    }

    @Step("断言用户信息字段一致")
    public void validateContent(User actual, User expected) {
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getAge()).isEqualTo(expected.getAge());
    }

    @Step("使用 Schema 校验响应体结构：{0}")
    public void validateSchema(ValidatableResponse response, String schemaFileName) {
        SchemaValidatorUtils.validate(response, schemaFileName);
    }

    // ---------------------- 数据提供器 ---------------------------

    @DataProvider(name = "csvUserProvider")
    public Object[][] csvUserProvider() throws Exception {
        String filePath = "src/test/resources/data/user_test_data.csv";
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                users.add(new User(
                        Integer.parseInt(parts[0]),  // id
                        parts[1],                   // name
                        parts[2],                   // email
                        Integer.parseInt(parts[3])  // age
                ));
            }
        }
        return users.stream().map(u -> new Object[]{u}).toArray(Object[][]::new);
    }

    @DataProvider(name = "jsonUserProvider")
    public Object[][] jsonUserProvider() throws Exception {
        String jsonPath = "src/test/resources/data/user_test_data.json";
        ObjectMapper mapper = new ObjectMapper();
        List<User> users = mapper.readValue(new File(jsonPath), new TypeReference<>() {});
        return users.stream().map(u -> new Object[]{u}).toArray(Object[][]::new);
    }
}
