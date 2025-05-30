package com.baidu.api.model;

import com.baidu.api.enums.AuthType;
import com.baidu.api.enums.HttpMethod;
import lombok.Data;

@Data
public class AuthCaseData {
    private String path;
    private String method;
    private AuthType authType;
    private int expectedStatus;
    private String schemaFile;

    public HttpMethod getHttpMethod() {
        return HttpMethod.fromString(this.method);
    }

}
