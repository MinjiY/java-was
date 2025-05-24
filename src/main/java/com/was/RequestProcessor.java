package com.was;

import com.was.config.ServerConfig;
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
            RequestValidatorChain.defaultChain().validate(httpRequest, httpResponse, target); // 403 에러
            SimpleServlet simpleServlet = RequestMapping.getServlet(httpRequest.getUri()); // TODO: 에러처리하기

            if (simpleServlet != null) {
                simpleServlet.service(httpRequest, httpResponse);
            }
            httpResponse.response(serverConfig.getVirtualHosts().get(host).getHttpRoot().toString(), serverConfig.getVirtualHosts().get(host).getErrorPage());
//            else if(simpleServlet == null) {
//                    // can't find the file
//                    String body = new StringBuilder("<HTML>\r\n")
//                            .append("<HEAD><TITLE>File Not Found</TITLE>\r\n")
//                            .append("</HEAD>\r\n")
//                            .append("<BODY>")
//                            .append("<H1>HTTP Error 404: File Not Found</H1>\r\n")
//                            .append("</BODY></HTML>\r\n")
//                            .toString();
//                    if (httpRequest.getVersion().startsWith("HTTP/")) { // send a MIME header
//                        sendHeader(out, "HTTP/1.0 404 File Not Found", "text/html; charset=utf-8", body.length());
//                    }
//                    out.write(body);
//                    out.flush();
//
//                }

//            } else {
//                // method does not equal "GET"
//                String body = new StringBuilder("<HTML>\r\n").append("<HEAD><TITLE>Not Implemented</TITLE>\r\n").append("</HEAD>\r\n")
//                        .append("<BODY>")
//                        .append("<H1>HTTP Error 501: Not Implemented</H1>\r\n")
//                        .append("</BODY></HTML>\r\n").toString();
//                if (version.startsWith("HTTP/")) { // send a MIME header
//                    sendHeader(out, "HTTP/1.0 501 Not Implemented",
//                            "text/html; charset=utf-8", body.length());
//                }
//                out.write(body);
//                out.flush();
//            }
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