package com.was;

import java.util.Map;

class ResponseUtils {

    static String formatResponseLine(String httpVersion, HttpStatus status) {
        return String.format("%s %s %s \r\n", httpVersion, status.getCode(), status.name());
    }

    static String formatResponseHeader(Map.Entry<String, String> header) {
        return String.format("%s: %s \r\n", header.getKey(), header.getValue());
    }
}
