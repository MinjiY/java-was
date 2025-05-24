package com.was;

import com.was.servlet.Hello;
import com.was.servlet.SimpleServlet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestMapping {
    public static Map<String, SimpleServlet> servletMap = new ConcurrentHashMap<>();

    static {
        servletMap.put("/Hello" , new Hello());
    }


    public static SimpleServlet getServlet(String uri) {
        return servletMap.get(uri);
    }

}
