package com.baidu.api.model;

public class ErrorCaseData {
    private String path;
    private String method;
    private String token;
    private int expectedStatus;
    private String expectedError;
    private String schemaFile;
    private boolean enabled;

    public ErrorCaseData() {}

    public ErrorCaseData(String path, String method, String token, int expectedStatus,
                         String expectedError, String schemaFile, boolean enabled) {
        this.path = path;
        this.method = method;
        this.token = token;
        this.expectedStatus = expectedStatus;
        this.expectedError = expectedError;
        this.schemaFile = schemaFile;
        this.enabled = enabled;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getExpectedStatus() {
        return expectedStatus;
    }

    public void setExpectedStatus(int expectedStatus) {
        this.expectedStatus = expectedStatus;
    }

    public String getExpectedError() {
        return expectedError;
    }

    public void setExpectedError(String expectedError) {
        this.expectedError = expectedError;
    }

    public String getSchemaFile() {
        return schemaFile;
    }

    public void setSchemaFile(String schemaFile) {
        this.schemaFile = schemaFile;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "ErrorCaseData{" +
                "path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", token='" + token + '\'' +
                ", expectedStatus=" + expectedStatus +
                ", expectedError='" + expectedError + '\'' +
                ", schemaFile='" + schemaFile + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
