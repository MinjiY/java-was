package Request;


import com.was.HttpResponse;
import com.was.HttpStatus;
import com.was.config.ServerConfig;

import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;


public class ResponseTest {
    private String testDirectory = "src/test/resources/";
    private ServerConfig serverConfig;

    @Before
    public void setUp() throws Exception {

        ServerConfig.load(Paths.get(testDirectory+"config/ServerConfig.json"));
        serverConfig = ServerConfig.getInstance();
    }

    @Test
    public void test_Response_403_error() throws IOException {
        String file = "Response/HTTP_403.html";
        HttpResponse response = new HttpResponse(new BufferedOutputStream(createOutputStream(file)));
        response.setStatus(HttpStatus.FORBIDDEN);
        response.response(testDirectory, serverConfig.getVirtualHosts().get("_default").getErrorPage());

        String expectedPath = testDirectory+file; // 윗줄에서 response를 통해 새로 생성된 파일
        String expectedString = Files.readString(Paths.get(expectedPath));

        // 응답을 직접 파일에 기록했으므로 split필요함, 응답 헤더와 바디는 \r\n\r\n 로 구분됨
        String body = expectedString.split("\r\n\r\n", 2)[1]; // 헤더 이후 바디

        String actualPath = testDirectory+"static/403.html";
        String actualString = Files.readString(Paths.get(actualPath)); // 에러용 html

        assertEquals(body, actualString);
    }


    @Test
    public void test_Response_404_error() throws IOException {
        String file = "Response/HTTP_404.html";
        HttpResponse response = new HttpResponse(new BufferedOutputStream(createOutputStream(file)));
        response.setStatus(HttpStatus.NOT_FOUND);
        response.response(testDirectory, serverConfig.getVirtualHosts().get("_default").getErrorPage());

        String expectedPath = testDirectory+file; // 윗줄에서 response를 통해 새로 생성된 파일
        String expectedString = Files.readString(Paths.get(expectedPath));

        // 응답을 직접 파일에 기록했으므로 split필요함, 응답 헤더와 바디는 \r\n\r\n 로 구분됨
        String body = expectedString.split("\r\n\r\n", 2)[1]; // 헤더 이후 바디

        String actualPath = testDirectory+"static/404.html";
        String actualString = Files.readString(Paths.get(actualPath)); // 에러용 html

        assertEquals(body, actualString);
    }

    @Test
    public void test_Response_500_error() throws IOException {
        String file = "Response/HTTP_500.html";
        HttpResponse response = new HttpResponse(new BufferedOutputStream(createOutputStream(file)));
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        response.response(testDirectory, serverConfig.getVirtualHosts().get("_default").getErrorPage());

        String expectedPath = testDirectory+file; // 윗줄에서 response를 통해 새로 생성된 파일
        String expectedString = Files.readString(Paths.get(expectedPath));

        // 응답을 직접 파일에 기록했으므로 split필요함, 응답 헤더와 바디는 \r\n\r\n 로 구분됨
        String body = expectedString.split("\r\n\r\n", 2)[1]; // 헤더 이후 바디

        String actualPath = testDirectory+"static/500.html";
        String actualString = Files.readString(Paths.get(actualPath)); // 에러용 html

        assertEquals(body, actualString);
    }

    private OutputStream createOutputStream(String filename) throws FileNotFoundException {
        return new FileOutputStream(new File(testDirectory + filename));
    }
}
