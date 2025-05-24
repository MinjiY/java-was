package com.was;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.was.ResponseUtils.formatResponseHeader;


public class HttpResponse {
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String EOF = "\r\n";
    private static final String CONTENT_TYPE = "text/html;charset=utf-8";
    private static Map<String, String> header = new ConcurrentHashMap<>();
    //private static header;
    private byte[] body;
    private HttpStatus status;

    private DataOutputStream out;

    public HttpResponse(OutputStream raw){
        this.out = new DataOutputStream(raw);
    }


    public void setBody(byte[] body){
        this.body = body;
    }
//    public void setHeaders(HttpStatus status, int length) {
//        StringBuilder headerString = new StringBuilder()
//                .append("HTTP/1.1 ")
//                .append(status.getCode()).append(' ')
//                .append(status.getMessage()).append(EOF)
//                .append("Date: ")
//                .append(new Date())
//                .append(EOF)
//                .append("Server: HTTP 2.0").append(EOF)
//                .append("Content-Length: ").append(length).append(EOF)
//                .append("Content-Type: ").append(CONTENT_TYPE).append(EOF)
//                .append(EOF);
//       header = headerString.toString();
//    }

    public void setHeader(ResponseHeaderAttribute attribute, String value){
        header.put(attribute.name(), value);
    }
    public void setHeader(ResponseHeaderAttribute attribute, int value){
        header.put(attribute.name(), String.valueOf(value));
    }
    public void setStatus(HttpStatus status){
        this.status = status;
    }
    public HttpStatus getStatus(){
        return this.status;
    }

    public void response(Path dir){
        try {
            byte[] responseBody = Files.readAllBytes(dir);
            out.writeBytes(HTTP_VERSION);
            out.writeBytes(EOF);
            header.put(String.valueOf(status.getCode()), HttpStatus.getMessageByCode(status.getCode()));
            header.put(ResponseHeaderAttribute.CONTENT_LENGTH.getAttribute(), String.valueOf(responseBody.length));
            header.put(ResponseHeaderAttribute.CONTENT_TYPE.getAttribute(), CONTENT_TYPE);
            Map<String, String> headers = header;
            for (Map.Entry<String, String> keyAndValue : headers.entrySet()) {
                out.writeBytes(formatResponseHeader(keyAndValue));
            }
            out.writeBytes(EOF);
            out.write(body, 0, body.length);
            out.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public void response(String hostRoot, Map<Integer, String> errorPage) {
        try {
            //out.writeBytes(HTTP_VERSION);
            if(status.getCode() != HttpStatus.OK.getCode()){
                System.out.println(hostRoot+errorPage.get(status.getCode()));
                body = Files.readAllBytes(Path.of(hostRoot+errorPage.get(status.getCode())));
                System.out.println("123");
            }
            System.out.println(hostRoot);
            System.out.println(hostRoot+errorPage.get(status.getCode()));
            out.writeBytes(HttpStatus.getMessageByCode(status.getCode())+EOF);
            header.put(ResponseHeaderAttribute.CONTENT_LENGTH.getAttribute(), String.valueOf(body.length));
            header.put(ResponseHeaderAttribute.CONTENT_TYPE.getAttribute(), CONTENT_TYPE);
            Map<String, String> headers = header;
            for (Map.Entry<String, String> keyAndValue : headers.entrySet()) {
                out.writeBytes(formatResponseHeader(keyAndValue));
            }
            out.writeBytes(EOF);
            out.write(body, 0, body.length);

            //byte[] theData = Files.readAllBytes(theFile.toPath());
//            if (version.startsWith("HTTP/")) { // send a MIME header
//                sendHeader(out, "HTTP/1.0 200 OK", contentType, theData.length);
//            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
