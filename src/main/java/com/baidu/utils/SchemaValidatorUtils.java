package com.baidu.utils;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class SchemaValidatorUtils {

    private static final Logger logger = LoggerFactory.getLogger(SchemaValidatorUtils.class);
    private static final String SCHEMA_DIR = "src/test/resources/schema/";
    private static final String SCHEMA_LOG_PATH = "target/schema-validation.log";

    @Step("执行 JSON Schema 校验：{schemaFileName}")
    public static void validate(ValidatableResponse response, String schemaFileName) {
        File schemaFile = new File(SCHEMA_DIR + schemaFileName);

        log("🔍 开始校验 Schema 文件: " + schemaFile.getName());
        log("📁 Schema 文件路径: " + schemaFile.getAbsolutePath());

        if (!schemaFile.exists()) {
            String msg = "❌ 未找到 Schema 文件: " + schemaFile.getAbsolutePath();
            log(msg);
            Allure.addAttachment("Schema 加载失败", msg);
            throw new AssertionError(msg);
        }

        if (schemaFile.length() == 0) {
            String msg = "⚠️ Schema 文件为空: " + schemaFile.getAbsolutePath();
            log(msg);
            Allure.addAttachment("Schema 加载失败", msg);
            throw new AssertionError(msg);
        }

        try {
            Allure.addAttachment("📦 Schema 文件内容", new FileInputStream(schemaFile));
        } catch (FileNotFoundException ignored) {}

        try {
            response.body(JsonSchemaValidator.matchesJsonSchema(schemaFile));
            log("✅ 通过 Schema 校验: " + schemaFileName);
        } catch (AssertionError e) {
            String failMsg = "❌ Schema 校验失败: " + e.getMessage();
            log(failMsg);
            Allure.addAttachment("Schema 校验失败", e.getMessage());
            throw new AssertionError(failMsg, e);
        } catch (Exception ex) {
            String errMsg = "❌ Schema 校验异常: " + ex.getMessage();
            log(errMsg);
            Allure.addAttachment("Schema 校验异常", errMsg);
            throw new RuntimeException(errMsg, ex);
        }
    }

    @Step("执行 JSON Schema 校验（自动拼接文件名）")
    public static void validate(String apiPath, int statusCode, Response response) {
        // 例：/users + 401 => users_401.json
        String normalizedPath = apiPath.replaceAll("[^a-zA-Z0-9]", "_");
        String schemaFileName = normalizedPath + "_" + statusCode + ".json";

        ValidatableResponse validatable = response.then();
        validate(validatable, schemaFileName);
    }

    @Step("根据状态码校验 Schema：{baseSchemaName}_{statusCode}.json")
    public static void validateByStatusCode(ValidatableResponse response, int statusCode, String baseSchemaName) {
        String fileName = String.format("%s_%d.json", baseSchemaName, statusCode);
        validate(response, fileName);
    }

    private static void log(String message) {
        logger.info(message);
        Allure.step(message);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCHEMA_LOG_PATH, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            logger.error("⚠️ 写入 schema log 失败: {}", e.getMessage());
        }
    }
}
