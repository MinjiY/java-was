package com.was.app1;

import com.was.HttpRequest;
import com.was.HttpResponse;
import com.was.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class HelloService extends HttpServelet {

    private static final Logger logger = LoggerFactory.getLogger(HelloService.class);
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setBody(("Hello").getBytes(StandardCharsets.UTF_8));
        httpResponse.setStatus(HttpStatus.OK);
    }
}
