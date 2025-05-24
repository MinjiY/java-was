package com.was.servlet;

import com.was.HttpRequest;
import com.was.HttpResponse;
import com.was.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Hello extends HttpServelet {
    private static final Logger logger = LoggerFactory.getLogger(Hello.class);
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        ZonedDateTime kstNow = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        System.out.println("KST ZonedDateTime : " + kstNow);
        String rfc1123Kst = DateTimeFormatter.RFC_1123_DATE_TIME.format(kstNow);
        httpResponse.setBody(rfc1123Kst.getBytes(StandardCharsets.UTF_8));
        httpResponse.setStatus(HttpStatus.OK);
    }
}
