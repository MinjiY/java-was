package com.was.exception;

public enum ExceptionCode {

    ACCESS_DENIED(403, "Access Denied"),
    RESOURCE_NOT_FOUND(404, "Resource Not Found"),
    INTERNAL(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented");

    private final int code;
    private final String message;
    ExceptionCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }
    public String getMessage() {
        return this.message;
    }
}


