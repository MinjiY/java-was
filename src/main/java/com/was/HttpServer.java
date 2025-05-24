package com.was;


import com.was.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by cybaek on 15. 5. 22..
 */
public class HttpServer {
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private static final String INDEX_FILE = "index.html";
    private static ServerConfig serverConfig;
    private static int NUM_THREADS;
    private final int port;

    public HttpServer(int port) throws IOException {
        this.port = port;
    }

    public void start() throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
        try (ServerSocket server = new ServerSocket(port)) {
            logger.info("Accepting connections on port " + server.getLocalPort());
            while (true) {
                try {
                    Socket request = server.accept();
                    Runnable r = new RequestProcessor(request);
                    pool.submit(r);
                } catch (IOException ex) {
                    logger.info("Error accepting connection", ex);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            // host 별 설정 관리하는 config 파일 로딩
            ServerConfig.load(Paths.get("src/main/resources/ServerConfig.json"));
            serverConfig = ServerConfig.getInstance();
        } catch (IOException | IllegalArgumentException e) {
            // 설정파일 로딩 실패시 부팅 x
            logger.error("Failed to read config", e);
            return;
        }
        NUM_THREADS = serverConfig.getThreads();
        int port = serverConfig.getPort();
            if (port <= 0 || port > 65535)
                port = 80;

        try {
            HttpServer webserver = new HttpServer(port);
            webserver.start();
        } catch (IOException ex) {
            logger.error("Server could not start", ex);
        }
    }
}