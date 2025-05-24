package com.was;

public class RequestLine {
    private HttpMethod method;
    private String RequestUri;
    private String HTTP_VERSION;

    private RequestLine(String requestLine){
        String[] splitLine = requestLine.split(" ");
        this.method = HttpMethod.getEnum(splitLine[0]);
        this.RequestUri = splitLine[1];
        if(splitLine.length > 2){
            this.HTTP_VERSION = splitLine[2];
        }
    }

    public static RequestLine of(String requestLine){
        return new RequestLine(requestLine);
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "method=" + method +
                ", RequestUri='" + RequestUri + '\'' +
                ", HTTP_VERSION='" + HTTP_VERSION + '\'' +
                '}';
    }
}
