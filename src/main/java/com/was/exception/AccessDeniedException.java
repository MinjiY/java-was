package com.was.exception;


public class AccessDeniedException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public AccessDeniedException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }


}
