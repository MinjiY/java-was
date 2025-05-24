package com.was.exception;

import com.was.HttpResponse;
import com.was.HttpStatus;

public class AccessDeniedException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public AccessDeniedException(ExceptionCode exceptionCode, HttpResponse response) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
        response.setStatus(HttpStatus.FORBIDDEN);
    }


}
