package com.was.app1.service;

import com.was.HttpRequest;
import com.was.HttpResponse;
import com.was.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class Hello extends HttpServelet {
    private static final Logger logger = LoggerFactory.getLogger(Hello.class);
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setBody(("Hello").getBytes(StandardCharsets.UTF_8));
        httpResponse.setStatus(HttpStatus.OK);
    }
}
