package com.was;

import com.was.config.ServerConfig;
import com.was.exception.AccessDeniedException;
import com.was.exception.ResourceNotFoundException;
import com.was.servlet.SimpleServlet;
import com.was.validator.URIValidatorChain;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestProcessor implements Runnable {
    private final static Logger logger = Logger.getLogger(RequestProcessor.class.getCanonicalName());
    private File rootDirectory;
    private String indexFileName = "index.html";
    private Socket connection;
    private ServerConfig serverConfig;

    public RequestProcessor(File rootDirectory, String indexFileName, Socket connection) {
        if (rootDirectory.isFile()) {
            throw new IllegalArgumentException(
                    "rootDirectory must be a directory, not a file");
        }
        try {
            rootDirectory = rootDirectory.getCanonicalFile();
        } catch (IOException ex) {
        }
        this.rootDirectory = rootDirectory;
        if (indexFileName != null)
            this.indexFileName = indexFileName;
        this.connection = connection;
        this.serverConfig = ServerConfig.getInstance();
    }

    @Override
    public void run() {
        // 명령에 관한 분기 전부 여기서 처리하기
        // for security checks
        String root = rootDirectory.getPath();
        try(
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "UTF-8"));
                OutputStream raw = new BufferedOutputStream(connection.getOutputStream()))
        {
            logger.info(connection.getRemoteSocketAddress().toString());
            String requestLine = in.readLine();
            System.out.println("requestLine = " + requestLine);
            String line;
            String host =""; // vHost
            while (!(line = in.readLine()).isEmpty()) {
                if (line.toLowerCase().startsWith("host:")) {
                    host = line.substring(5).trim().split(":")[0];
                }
            }
            host =  (host.equals("localhost") || host.equals("127.0.0.1")) ?  "_default" : host;
            HttpRequest httpRequest = new HttpRequest(requestLine);
            HttpResponse httpResponse = new HttpResponse(raw);
            Path target = serverConfig.getVirtualHosts().get(host).getHttpRoot().resolve(httpRequest.getUri()).normalize();
            try {
                // 1. 유효성 검사
                URIValidatorChain.defaultChain().validate(httpRequest, target);
                // 2. 서블릿 로딩 및 실행
                SimpleServlet simpleServlet = RequestMapping.getServlet(httpRequest.getUri());
                if (simpleServlet != null) {
                    simpleServlet.service(httpRequest, httpResponse);
                }

            } catch (AccessDeniedException e) {
                httpResponse.setStatus(HttpStatus.FORBIDDEN);
                //httpResponse.setBody(("<h1>403 Forbidden</h1>").getBytes());
                logger.warning("Access denied: " + e.getMessage());
            } catch (ResourceNotFoundException e) {
                httpResponse.setStatus(HttpStatus.NOT_FOUND);
                //httpResponse.setBody(("<h1>404 Not Found</h1>").getBytes());
                logger.warning("Resource not found: " + e.getMessage());
            } catch (Exception e) {
                httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                //httpResponse.setBody(("<h1>500 Internal Server Error</h1>").getBytes());
                logger.log(Level.SEVERE, "Unhandled exception", e);
            }
            httpResponse.response(serverConfig.getVirtualHosts().get(host).getHttpRoot().toString(), serverConfig.getVirtualHosts().get(host).getErrorPage());
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Error talking to " + connection.getRemoteSocketAddress(), ex);
        } finally {
            try {
                connection.close();
            } catch (IOException ex) {
            }
        }
    }
}