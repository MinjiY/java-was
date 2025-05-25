package BootTest;

import com.was.HttpStatus;
import com.was.RequestProcessor;
import com.was.config.ServerConfig;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BootTest {
    private final String testDirectory = "src/test/resources/";
    private Socket fakeSocket;
    private ByteArrayOutputStream responseStream;
    private static ServerConfig serverConfig;

    @BeforeClass
    public static void setUpClass() throws Exception {
        // test용 config load
        ServerConfig.load(Paths.get("src/test/resources/config/ServerConfig.json"));
        serverConfig = ServerConfig.getInstance();
    }

    @Before
    public void setUp() throws IOException {
        fakeSocket = mock(Socket.class);
        responseStream = new ByteArrayOutputStream();
        when(fakeSocket.getOutputStream()).thenReturn(responseStream);
    }

    @Test
    public void requestAccessParent() throws IOException {
        String requestFile = "Request/HTTP_AccessParent.txt";
        responseTest(requestFile, HttpStatus.FORBIDDEN);
    }

    @Test
    public void requestExecutionFile() throws IOException {
        String requestFile = "Request/HTTP_ExecutionFile.txt";
        responseTest(requestFile, HttpStatus.FORBIDDEN);
    }

    @Test
    public void requestNotSupported() throws IOException {
        String requestFile = "Request/HTTP_Method.txt";
        responseTest(requestFile, HttpStatus.NOT_IMPLEMENTED);
    }

    @Test
    public void requestNotFound() throws IOException {
        String requestFile = "Request/HTTP_NotFound.txt";
        responseTest(requestFile, HttpStatus.NOT_FOUND);
    }

    public void responseTest(String requestFile, HttpStatus httpStatus) throws IOException {
        // given
        setRequest(requestFile); // RequestLine 파일

        RequestProcessor processor = new RequestProcessor(fakeSocket);

        // when
        processor.run();

        // then
        String responseBody = getBody(responseStream.toString(StandardCharsets.UTF_8));
        String actualBody = actualBody(testDirectory, serverConfig.getVirtualHosts().get("_default").getErrorPage(), httpStatus.getCode());
        assertEquals(responseBody, actualBody);
    }

    private void setRequest(String fileName) throws IOException {
        // 메모리 사용
        ByteArrayInputStream requestStream = new ByteArrayInputStream(
                Files.readAllBytes(Paths.get(testDirectory + fileName)));
        when(fakeSocket.getInputStream()).thenReturn(requestStream);
    }

    private String actualBody(String file, Map<Integer, String> errorPage, Integer code) throws IOException {
        return Files.readString(Paths.get(file+ errorPage.get(code))); // 에러용 html
    }

    private String getBody(String response){
        return response.split("\r\n\r\n", 2)[1]; // 헤더 이후 바디
    }
}
