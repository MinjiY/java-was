package com.was;


import com.was.config.ServerConfig;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by cybaek on 15. 5. 22..
 */
public class HttpServer {
    private static final Logger logger = Logger.getLogger(HttpServer.class.getCanonicalName());
    private static final String INDEX_FILE = "index.html";
    private static ServerConfig serverConfig;
    private static int NUM_THREADS;
    private final File rootDirectory;
    private final int port;

    public HttpServer(File rootDirectory, int port) throws IOException {
        if (!rootDirectory.isDirectory()) {
            throw new IOException(rootDirectory
                    + " does not exist as a directory");
        }
        this.rootDirectory = rootDirectory;
        this.port = port;
    }

    public void start() throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
        try (ServerSocket server = new ServerSocket(port)) {
            logger.info("Accepting connections on port " + server.getLocalPort());
            logger.info("Document Root: " + rootDirectory);
            while (true) {
                try {
                    Socket request = server.accept();
                    Runnable r = new RequestProcessor(rootDirectory, INDEX_FILE, request);
                    pool.submit(r);
                } catch (IOException ex) {
                    logger.log(Level.WARNING, "Error accepting connection", ex);
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
            // 필수 값 설정파일에 들어있지 않으면 부팅 x
            logger.log(Level.SEVERE, "Failed to read config", e);
            return;
        }
        System.out.println(serverConfig.toString());
        NUM_THREADS = serverConfig.getThreads();
        File docroot = new File(serverConfig.getDocumentRoot().toUri());
        int port = serverConfig.getPort();
            if (port <= 0 || port > 65535)
                port = 80;

        try {
            HttpServer webserver = new HttpServer(docroot, port);
            webserver.start();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Server could not start", ex);
        }
    }
}