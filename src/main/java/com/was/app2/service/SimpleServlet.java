package com.was.app2.service;

import com.was.HttpRequest;
import com.was.HttpResponse;

public interface SimpleServlet {
    void service(HttpRequest req, HttpResponse res);
}
