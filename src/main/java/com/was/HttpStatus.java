package com.was;

public enum HttpStatus {
    OK(200, "HTTP/1.1 200 OK"),
    FORBIDDEN(403, "HTTP/1.1 403 Forbidden"),
    NOT_FOUND(404, "HTTP/1.1 404 File Not Found"),
    INTERNAL_SERVER_ERROR(500, "HTTP/1.1 501 Not Implemented");

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
    public static String getMessageByCode(int code) {
        for (HttpStatus status : values()) {
            if (status.code == code) {
                return status.message;
            }
        }
        throw new IllegalArgumentException("Unknown HTTP status code: " + code);
    }

}
