package com.baidu.api.tests;

import com.baidu.api.base.BaseApiTest;
import com.baidu.api.model.User;
import com.baidu.utils.SchemaValidatorUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
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

public class UserApiTest extends BaseApiTest {

    private static final String SCHEMA_USER = "user_schema.json";
    private static final String SCHEMA_LIST = "user_list_schema.json";

    @Test(dataProvider = "csvUserProvider")
    @Description("从 CSV 文件读取用户数据，创建用户并验证响应 + Schema 校验")
    public void testCreateUserFromCsv(User user) {
        ValidatableResponse response = createUserAndReturnResponse(user);
        validateSchema(response, SCHEMA_USER);
        validateContent(response.extract().as(User.class), user);
    }

    @Test(dataProvider = "jsonUserProvider")
    @Description("从 JSON 文件读取用户数据，创建用户并验证响应 + Schema 校验")
    public void testCreateUserFromJson(User user) {
        ValidatableResponse response = createUserAndReturnResponse(user);
        validateSchema(response, SCHEMA_USER);
        validateContent(response.extract().as(User.class), user);
    }

    @Test
    @Description("验证 GET /users 接口返回结构 schema")
    public void testGetUserListSchemaValidation() {
        ValidatableResponse response = given()
                .auth().oauth2(getToken())
                .get(BASE_URL + "/users")
                .then()
                .statusCode(200);

        SchemaValidatorUtils.validate(response, SCHEMA_LIST);
    }

    // ------------------------ 工具方法 ----------------------------

    @Step("创建用户并返回响应")
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

    @Step("断言创建用户的字段一致性")
    public void validateContent(User actual, User expected) {
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getAge()).isEqualTo(expected.getAge());
    }

    @Step("校验响应符合 JSON Schema: {0}")
    public void validateSchema(ValidatableResponse response, String schemaFileName) {
        SchemaValidatorUtils.validate(response, schemaFileName);
    }

    // ---------------------- 数据提供器 ---------------------------

    @DataProvider(name = "csvUserProvider")
    public Object[][] csvUserProvider() throws Exception {
        String filePath = "src/test/resources/data/user_test_data.csv";
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                users.add(new User(
                        Integer.parseInt(parts[0]),
                        parts[1],
                        parts[2],
                        Integer.parseInt(parts[3])
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
