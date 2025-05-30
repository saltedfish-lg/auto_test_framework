package com.baidu.api.model;

import com.baidu.api.enums.AuthType;
import com.baidu.api.enums.HttpMethod;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class AuthCaseData {

    private String path;              // 请求路径，如 /users
    private String method;            // GET / POST / PUT
    private AuthType authType;        // BEARER / COOKIE / BASIC / NONE
    private int expectedStatus;       // 预期响应状态码，如 200、401、404
    private String expectedError;     // （可选）错误信息断言内容
    private String schemaFile;        // （可选）用于响应结构校验的 JSON Schema 文件

    /**
     * 获取 HTTP 方法枚举，支持大小写容错
     */
    public HttpMethod getHttpMethod() {
        try {
            return HttpMethod.fromString(method);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("⚠️ 无效的 HTTP 方法: " + method, e);
        }
    }

    /**
     * 是否包含 error 字段断言
     */
    public boolean hasErrorAssertion() {
        return StringUtils.isNotBlank(expectedError);
    }

    /**
     * 是否指定了 schema 校验文件
     */
    public boolean hasSchemaFile() {
        return StringUtils.isNotBlank(schemaFile);
    }

    /**
     * 格式化为调试信息输出
     */
    @Override
    public String toString() {
        return String.format("[Method=%s] [Auth=%s] [Path=%s] [Expect=%d] [Error='%s'] [Schema=%s]",
                method, authType, path, expectedStatus,
                expectedError != null ? expectedError : "无",
                schemaFile != null ? schemaFile : "未指定");
    }
}
