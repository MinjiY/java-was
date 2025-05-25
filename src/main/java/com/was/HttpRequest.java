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

    public HttpRequest(BufferedReader in) throws IOException{
        readLine(in);
    }

    private void readLine(BufferedReader in) throws IOException{
        requestLine = in.readLine();
        String line="";;
        String host =""; // vHost
        while (!(line = in.readLine()).isEmpty()) {
            if (line.toLowerCase().startsWith("host:")) {
                host = line.substring(5).trim().split(":")[0];
            }
        }
        host =  (host.equals("localhost") || host.equals("127.0.0.1")) ?  "_default" : host;

        // 지원하는 host 확인?
        headers.put("Host", host);

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

    public Map<String, String> getHeaders(){
        return this.headers;
    }

}
