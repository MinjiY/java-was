package com.was.exception;

public enum ExceptionCode {

    BAD_REQUEST(400, "Bad Request"),
    ACCESS_DENIED(403, "Access Denied"),
    RESOURCE_NOT_FOUND(404, "Resource Not Found"),
    INTERNAL(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Method Not Supported");

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


