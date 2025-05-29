package com.baidu.api.steps;

import com.baidu.api.model.User;
import com.baidu.utils.ConfigUtils;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

public class CreateUserSteps {

    private final String BASE_URL = ConfigUtils.getProperty("api.base.url", "http://localhost:8080/api");
    private final String TOKEN = ConfigUtils.getProperty("api.token", "");

    @Step("创建用户: {user.name} - {user.email}")
    public User createUser(User user) {
        return given()
                .baseUri(BASE_URL)
                .basePath("/users")
                .contentType(ContentType.JSON)
                .auth().oauth2(TOKEN)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract().as(User.class);
    }
}
