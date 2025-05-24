package com.was.validator;

import com.was.HttpRequest;
import com.was.HttpResponse;

import java.nio.file.Path;

@FunctionalInterface
public interface RequestValidator {
    void validate(HttpRequest request, HttpResponse response, Path target);
}
