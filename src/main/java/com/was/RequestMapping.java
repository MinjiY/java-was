package com.was;

import com.was.config.ServerConfig;
import com.was.exception.ExceptionCode;
import com.was.exception.ResourceNotFoundException;
import com.was._default.KSTTime;
import com.was._default.SimpleServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestMapping {
    private static final Logger logger = LoggerFactory.getLogger(RequestMapping.class);
    public static Map<String, SimpleServlet> servletMap = new ConcurrentHashMap<>();
    public static ServerConfig serverConfig;

    static {
        servletMap.put("/KSTTime" , new KSTTime());
        serverConfig = ServerConfig.getInstance();
    }


    public static SimpleServlet getServlet(String uri)
    {
        // 미리 정의해둔 클래스
        if (servletMap.containsKey(uri)) {
            return servletMap.get(uri);
        }

        // 동적 로딩한 클래스
        SimpleServlet servlet = loadServletDynamically(uri);
        if (servlet != null) {
            servletMap.put(uri, servlet);
        }

        return servlet;
    }

    private static SimpleServlet loadServletDynamically(String uri){
        // '/' 제거 후 '.' 기준으로 패키지와 클래스 분리
        String cleanedUri = uri.startsWith("/") ? uri.substring(1) : uri;
        String rootPackage = "com.was";
        try {
            Class<?> clazz = Class.forName(rootPackage + "." + cleanedUri);
            return (SimpleServlet) clazz.getDeclaredConstructor().newInstance();
        }catch (Exception e) {
            throw new ResourceNotFoundException(ExceptionCode.RESOURCE_NOT_FOUND);
        }
    }

}
