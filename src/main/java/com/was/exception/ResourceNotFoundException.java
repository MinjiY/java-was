package com.was.exception;

import com.was.HttpResponse;
import com.was.HttpStatus;

public class ResourceNotFoundException extends RuntimeException{
    private final ExceptionCode exceptionCode;

    public ResourceNotFoundException(ExceptionCode exceptionCode, HttpResponse response) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
        response.setStatus(HttpStatus.NOT_FOUND);
    }
}
