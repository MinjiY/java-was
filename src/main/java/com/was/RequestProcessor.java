package com.was;

import com.was.config.ServerConfig;
import com.was.exception.AccessDeniedException;
import com.was.exception.ExceptionCode;
import com.was.exception.NotSupportedHttpMethodException;
import com.was.exception.ResourceNotFoundException;
import com.was._default.SimpleServlet;
import com.was.validator.URIValidatorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;

public class RequestProcessor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestProcessor.class);
    private Socket connection;
    private ServerConfig serverConfig;

    public RequestProcessor(Socket connection) {
        this.connection = connection;
        this.serverConfig = ServerConfig.getInstance();
    }

    @Override
    public void run() {
        // 명령에 관한 분기 전부 여기서 처리하기
        // for security checks
        try(
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "UTF-8"));
                OutputStream raw = new BufferedOutputStream(connection.getOutputStream()))
        {

            HttpRequest httpRequest;
            String host= "_default";
            HttpResponse httpResponse = new HttpResponse(raw);
            try {

                httpRequest = new HttpRequest(in);
                host = httpRequest.getHeaders().get("Host");

                logger.info("Request received from Host: {}", host);
                Path target = serverConfig.getVirtualHosts().get(host).getHttpRoot().resolve(httpRequest.getUri()).normalize();

                // 1. 유효성 검사
                URIValidatorChain.defaultChain().validate(httpRequest, target);
                // 2. 서블릿 로딩 및 실행
                SimpleServlet simpleServlet = RequestMapping.getServlet(httpRequest.getUri());
                if (simpleServlet != null) {
                    simpleServlet.service(httpRequest, httpResponse);
                }

            } catch (AccessDeniedException e) {
                httpResponse.setStatus(HttpStatus.FORBIDDEN);
                logger.debug(ExceptionCode.ACCESS_DENIED.getMessage(), e);
            } catch (ResourceNotFoundException e) {
                httpResponse.setStatus(HttpStatus.NOT_FOUND);
                logger.debug(ExceptionCode.RESOURCE_NOT_FOUND.getMessage(), e);
            }catch(NotSupportedHttpMethodException e){
                httpResponse.setStatus(HttpStatus.NOT_IMPLEMENTED);
                logger.debug(ExceptionCode.NOT_IMPLEMENTED.getMessage(), e);
            }catch (IllegalArgumentException e){
                httpResponse.setStatus(HttpStatus.BAD_REQUEST);
                logger.debug(ExceptionCode.BAD_REQUEST.getMessage(), e);
            }
            catch (Exception e) {
                httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                logger.debug(ExceptionCode.INTERNAL.getMessage(), e);
            }
            httpResponse.response(serverConfig.getVirtualHosts().get(host).getHttpRoot().toString(), serverConfig.getVirtualHosts().get(host).getErrorPage());
        } catch (IOException ex) {
            logger.warn("Error talking to " + connection.getRemoteSocketAddress(), ex);
        } finally {
            try {
                connection.close();
            } catch (IOException ex) {
            }
        }
    }
}