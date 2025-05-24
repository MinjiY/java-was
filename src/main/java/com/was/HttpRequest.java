package com.was;

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

    private void readLine() throws IOException {
        String[] splitLine = requestLine.split(" ");
        this.method = HttpMethod.valueOf(splitLine[0]);
        System.out.println(splitLine[0]);
        System.out.println(splitLine[1]);
        this.Uri = splitLine[1];
        if(splitLine.length > 2){
            version = splitLine[2];
        }
        // get, post, put 등 확인
        // get이면서 ?이 있으면 queryParam 확인
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
