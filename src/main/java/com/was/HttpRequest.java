package com.was;

import com.was.exception.ExceptionCode;
import com.was.exception.NotSupportedHttpMethodException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();
    // HTTP 요청에 대한 상태를 가질 수 없음
     private String Uri;
     private HttpMethod method;
     private String version;
     private String requestLine;

//    public HttpRequest(InputStream in) throws IOException {
//        readLine(new BufferedReader(new InputStreamReader(in, "UTF-8")));
//    }
    public HttpRequest(String requestLine) throws IOException{
        this.requestLine = requestLine;
        readLine();
    }

    private void readLine(){
        // method, uri, version
        String[] splitLine = requestLine.split(" ");
        this.method = HttpMethod.valueOf(splitLine[0]);
        this.Uri = splitLine[1];
        if(splitLine.length > 2){
            version = splitLine[2];
        }
        if(method != HttpMethod.GET){
            throw new NotSupportedHttpMethodException(ExceptionCode.NOT_IMPLEMENTED);
        }

        // uri ?
    }

    public HttpMethod getMethod(){
        return this.method;
    }

    public String getUri(){
        return this.Uri;
    }

    public String getVersion(){
        return this.version;
    }

}
