package com.baidu.api.base;

import com.baidu.api.enums.AuthType;
import com.baidu.utils.ConfigUtils;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

/**
 * 接口测试统一基础类，集成基础配置和多种鉴权方式支持（Bearer、Cookie、Basic、匿名）
 */
public class BaseApiTest {

    protected static final Logger logger = LoggerFactory.getLogger(BaseApiTest.class);

    // 🔧 统一读取 BASE_URL（默认 fallback 为 localhost）
    protected static final String BASE_URL = ConfigUtils.getProperty("api.base.url", "http://localhost:8080/api");

    /**
     * 默认获取 Bearer Token（配置项：api.auth.token）
     */
    protected String getToken() {
        return ConfigUtils.getToken();
    }

    protected String getCookie() {
        return ConfigUtils.getCookie();
    }

    protected String getBasicUser() {
        return ConfigUtils.getBasicUsername();
    }

    protected String getBasicPass() {
        return ConfigUtils.getBasicPassword();
    }

    /**
     * 获取 Bearer Token 请求
     */
    protected RequestSpecification getBaseRequest() {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + getToken());
    }

    /**
     * 获取 Cookie 鉴权的请求
     */
    protected RequestSpecification getCookieRequest() {
        return given()
                .baseUri(BASE_URL)
                .header("Cookie", getCookie());
    }

    /**
     * 获取 Basic Auth 鉴权的请求
     */
    protected RequestSpecification getBasicAuthRequest() {
        return given()
                .baseUri(BASE_URL)
                .auth().preemptive().basic(getBasicUser(), getBasicPass());
    }

    /**
     * 获取无认证请求（匿名）
     */
    protected RequestSpecification getNoAuthRequest() {
        return given().baseUri(BASE_URL);
    }

    /**
     * 🔁 按枚举类型选择鉴权方式（推荐）
     */
    protected RequestSpecification getRequestByAuth(AuthType authType) {
        if (authType == null) {
            logger.warn("⚠️ AuthType 为空，默认使用匿名请求");
            return getNoAuthRequest();
        }

        return switch (authType) {
            case BEARER, OAUTH2 -> getBaseRequest();
            case COOKIE -> getCookieRequest();
            case BASIC -> getBasicAuthRequest();
            case NONE -> getNoAuthRequest();
            default -> {
                logger.warn("⚠️ 未识别的 AuthType [{}]，默认使用匿名请求", authType);
                yield getNoAuthRequest();
            }
        };
    }

    /**
     * 支持 String 参数（兼容 Excel/动态调用）
     */
    protected RequestSpecification getRequestByAuth(String authTypeStr) {
        if (authTypeStr == null || authTypeStr.trim().isEmpty()) {
            logger.warn("⚠️ 未提供鉴权类型字符串，使用匿名请求");
            return getNoAuthRequest();
        }

        try {
            AuthType authType = AuthType.valueOf(authTypeStr.trim().toUpperCase());
            return getRequestByAuth(authType);
        } catch (IllegalArgumentException e) {
            logger.warn("⚠️ 鉴权字符串无法解析为枚举 [{}]，默认使用匿名请求", authTypeStr);
            return getNoAuthRequest();
        }
    }
}
