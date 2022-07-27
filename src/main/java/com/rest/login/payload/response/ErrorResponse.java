package com.rest.login.payload.response;

public class ErrorResponse {
    private String type;
    private String fieldName;
    private String cause;

    public ErrorResponse(String type, String fieldName, String cause) {
        this.type = type;
        this.fieldName = fieldName;
        this.cause = cause;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}
