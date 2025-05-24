package com.was.servlet;

import com.was.HttpRequest;
import com.was.HttpResponse;

public interface SimpleServlet {
    void service(HttpRequest req, HttpResponse res);
}
