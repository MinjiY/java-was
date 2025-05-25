package com.was;

import com.was._default.HelloService;
import com.was.config.ServerConfig;
import com.was.exception.ExceptionCode;
import com.was.exception.ResourceNotFoundException;
import com.was._default.KSTTime;
import com.was._default.SimpleServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RequestMapping {
    private static final Logger logger = LoggerFactory.getLogger(RequestMapping.class);
    public static Map<String, SimpleServlet> servletMap = new ConcurrentHashMap<>();
    public static ServerConfig serverConfig;

    static {
        servletMap.put("/HelloService" , new HelloService());
        servletMap.put("/KSTTime", new KSTTime());

        serverConfig = ServerConfig.getInstance();

        // 외부 Jar에 있는 class 최초 한번 로딩
        List<SimpleServlet> servletList = loadServletsFromJars();
        for (SimpleServlet service : servletList) {
            String className = service.getClass().getName();
            String newServiceUri = "/" + className.substring(className.lastIndexOf('.') + 1);
            servletMap.put(newServiceUri, service);
            logger.info("Loading Jar: {}", className);
        }
    }


    public static SimpleServlet getServlet(String host, String uri)
    {
        // 미리 정의해둔 클래스
        if (servletMap.containsKey(uri)) {
            return servletMap.get(uri);
        }

        // 동적 로딩한 클래스
        SimpleServlet servlet = loadServletDynamically(host, uri);
        if (servlet != null) {
            servletMap.put(uri, servlet);
        }

        return servlet;
    }

    private static SimpleServlet loadServletDynamically(String host, String uri){
        // '/' 제거 후 '.' 기준으로 패키지와 클래스 분리
        String cleanedUri = uri.startsWith("/") ? uri.substring(1) : uri;
        logger.info("요청 URI: {}", cleanedUri);
        String rootPackage = "com.was";
        String hostRoot = serverConfig.getVirtualHosts().get(host).getHttpRoot().toString();
        String rootSlashToDot = hostRoot.replace('/', '.').replace('\\', '.');
        int packageStart = rootSlashToDot.indexOf(rootPackage);
        String classPath = rootSlashToDot.substring(packageStart);
        logger.info("새로 등록할 class: {}", classPath + "." + cleanedUri);
        try {
            Class<?> clazz = Class.forName(classPath + "." + cleanedUri);
            return (SimpleServlet) clazz.getDeclaredConstructor().newInstance();
        }catch (Exception e) {
            throw new ResourceNotFoundException(ExceptionCode.RESOURCE_NOT_FOUND);
        }
    }

    public static List<SimpleServlet> loadServletsFromJars() {
        List<SimpleServlet> servletList = new ArrayList<>();
        String appDir = "app/";
        File libDir = new File(appDir);
        if (!libDir.exists() || !libDir.isDirectory()) {
            logger.info("{} 경로가 존재하지 않음", appDir);
            return servletList;
        }

        File[] jarFiles = libDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null || jarFiles.length == 0) {
            logger.info("{} 경로에 Jar가 존재하지 않음", appDir);
            return servletList;
        }

        for (File jarFile : jarFiles) {
            logger.info("Loading JAR: {}", jarFile.getName());
            try (JarFile jar = new JarFile(jarFile);
                 URLClassLoader loader = new URLClassLoader(
                         new URL[]{jarFile.toURI().toURL()},
                         RequestMapping.class.getClassLoader())) {

                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();

                    if (entry.getName().endsWith(".class")) { // /를 .으로
                        String className = entry.getName()
                                .replace('/', '.')
                                .replace('\\', '.')
                                .replace(".class", "");

                        try {
                            Class<?> clazz = loader.loadClass(className);
                            Object instance = clazz.getDeclaredConstructor().newInstance();
                            servletList.add((SimpleServlet) instance);
                            logger.info("Jar 등록됨 : {}", className);
                        } catch (Exception e) {
                            logger.info("Jar 로딩 실패: {} (class: {})", jarFile.getName(), className);
                        }
                    }
                }

            } catch (IOException e) {
                logger.error("Failed Loding class externalJar" , e);
            }
        }

        return servletList;
    }
}


