package com.was;

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
        throw new IllegalArgumentException();
    }

}
