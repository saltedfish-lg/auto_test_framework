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
        log("🔍 准备校验 Schema 文件: " + schemaFileName);

        if (!schemaFile.exists()) {
            String msg = "❌ 未找到 Schema 文件: " + schemaFile.getAbsolutePath();
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
            Allure.step("✅ 通过 Schema 校验");
        } catch (AssertionError e) {
            log("❌ Schema 校验失败: " + e.getMessage());
            Allure.addAttachment("Schema 校验失败", e.getMessage());
            throw new AssertionError("❌ JSON Schema 校验失败: " + e.getMessage(), e);
        }
    }

    @Step("执行 JSON Schema 校验（自动拼接文件名）")
    public static void validate(String apiPath, int statusCode, Response response) {
        // e.g. "/users" + "_" + 401.json => users_401.json
        String normalizedPath = apiPath.replaceAll("[^a-zA-Z0-9]", "_"); // 替换为安全文件名
        String schemaFileName = normalizedPath + "_" + statusCode + ".json";

        // 转换 Response 为 ValidatableResponse
        ValidatableResponse validatable = response.then();

        validate(validatable, schemaFileName);
    }


    /**
     * 动态加载：如传入 baseName=user，status=200，则加载 user_200.json
     */
    @Step("根据状态码校验 Schema：{baseSchemaName}_{statusCode}.json")
    public static void validateByStatusCode(ValidatableResponse response, int statusCode, String baseSchemaName) {
        String fileName = String.format("%s_%d.json", baseSchemaName, statusCode);
        validate(response, fileName);
    }

    private static void log(String message) {
        logger.info(message);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCHEMA_LOG_PATH, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            logger.error("⚠️ 写入 schema log 失败: {}", e.getMessage());
        }
    }
}
