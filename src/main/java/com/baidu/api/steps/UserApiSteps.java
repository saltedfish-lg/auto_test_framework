package com.baidu.api.steps;

import com.baidu.api.base.ApiStepsBase;
import com.baidu.api.model.User;
import com.baidu.api.model.UserInput;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 用户相关接口操作步骤
 */
public class UserApiSteps extends ApiStepsBase {

    private static final String BASE_PATH = "/users";
    private static final ObjectMapper mapper = new ObjectMapper();

    @Step("获取用户列表")
    public List<User> getUserList(String token) throws Exception {
        Response res = withAuth(token)
                .get(BASE_PATH)
                .then()
                .statusCode(200)
                .extract()
                .response();

        return mapper.readValue(res.asInputStream(), new TypeReference<>() {});
    }

    @Step("创建用户: {0}")
    public User createUser(String token, UserInput userInput) {
        return withAuth(token)
                .body(userInput)
                .post(BASE_PATH)
                .then()
                .statusCode(201)
                .extract()
                .as(User.class);
    }

    @Step("根据 ID 获取用户信息: {0}")
    public User getUserById(String token, int id) {
        return withAuth(token)
                .get(BASE_PATH + "/" + id)
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);
    }

    @Step("更新用户 ID: {0}")
    public User updateUser(String token, int id, UserInput input) {
        return withAuth(token)
                .body(input)
                .put(BASE_PATH + "/" + id)
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);
    }

    @Step("删除用户 ID: {0}")
    public void deleteUser(String token, int id) {
        withAuth(token)
                .delete(BASE_PATH + "/" + id)
                .then()
                .statusCode(204);
    }
}
