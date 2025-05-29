package com.baidu.api.base;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * 接口步骤基类：提供统一认证和请求配置
 */
public abstract class ApiStepsBase {

    /**
     * 创建带认证的请求规范
     * @param token JWT Token
     * @return 已设置 Bearer Token 与 Content-Type 的请求规范
     */
    protected RequestSpecification withAuth(String token) {
        return given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .log().all(); // 可选：开启请求日志
    }

    /**
     * 创建无认证的公共请求（如登录、注册）
     */
    protected RequestSpecification withoutAuth() {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .log().all(); // 可选
    }
}
