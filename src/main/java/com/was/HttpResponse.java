package com.was;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpResponse {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";
    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private static Map<String, String> header = new ConcurrentHashMap<>();
    private byte[] body;
    private HttpStatus status;

    private DataOutputStream out;

    public HttpResponse(OutputStream raw){
        this.out = new DataOutputStream(raw);
    }


    public void setBody(byte[] body){
        this.body = body;
    }

    public void setHeader(ResponseHeaderAttribute attribute, String value){
        header.put(attribute.name(), value);
    }
    public void setHeader(ResponseHeaderAttribute attribute, int value){
        header.put(attribute.name(), String.valueOf(value));
    }
    public void setStatus(HttpStatus status){
        this.status = status;
    }

    public void response(String hostRoot, Map<Integer, String> errorPage) {
        try {
            if (status.getCode() != HttpStatus.OK.getCode()) {
                String errorPath = errorPage.get(status.getCode());
                if (errorPath != null) {
                    Path fullPath = Path.of(hostRoot, errorPath);
                    if (Files.exists(fullPath)) {
                        setBody(Files.readAllBytes(fullPath));
                    }
                }
            }
            setHeader(ResponseHeaderAttribute.DATE, String.valueOf(new Date()));
            setHeader(ResponseHeaderAttribute.CONTENT_LENGTH, body.length);
            setHeader(ResponseHeaderAttribute.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);

            writeResponse();
        } catch (IOException e) {
            logger.info("Failed to write HTTP response to client (status= {}: )", status.getCode(), e);
        }
    }
        private void writeResponse() throws IOException {
            // Status Line
            out.writeBytes(HTTP_VERSION + " " + status.getCode() + " " + status.getMessage() + CRLF);

            // Headers
            for (Map.Entry<String, String> entry : header.entrySet()) {
                out.writeBytes(entry.getKey() + ": " + entry.getValue() + CRLF);
            }
            // header 와 body 사이 빈줄 필요
            out.writeBytes(CRLF);

            // Body
            out.write(body);
            out.flush();

    }
}
