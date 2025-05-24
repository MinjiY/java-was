package com.was;

import java.util.Map;

class ResponseUtils {

    static String formatResponseHeader(Map.Entry<String, String> header) {
        return String.format("%s: %s \r\n", header.getKey(), header.getValue());
    }
}
