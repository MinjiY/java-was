package com.was;

import java.util.Arrays;

public enum HttpMethod {
    GET("GET"), POST("POST");

    private String method;

    private HttpMethod(String method){
        this.method = method;
    }

    public String getMethod(){
        return this.method;
    }

    public static HttpMethod getEnum(String method) {

        for (HttpMethod httpMethod : HttpMethod.values()) {
            if (httpMethod.getMethod().equals(method)) {
                return httpMethod;
            }
        }
        throw new IllegalArgumentException("요청 라인에서 알 수 없는 메서드 '" + method + "' 가 감지되었습니다. 지원되는 메서드: " + Arrays.toString(HttpMethod.values()));
    }

}
