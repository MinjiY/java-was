package com.was.exception;

public class NotSupportedHttpMethodException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public NotSupportedHttpMethodException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}
