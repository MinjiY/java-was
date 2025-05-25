package Request;

import com.was.HttpMethod;
import com.was.HttpRequest;
import com.was.exception.NotSupportedHttpMethodException;
import org.hamcrest.MatcherAssert;

import org.junit.Test;

import java.io.*;

import static org.hamcrest.Matchers.is;
public class RequestTest {

//    private HttpRequest request;
    private String testDirectory = "src/test/resources/Request/";

//    @Before
//    public void setUp() throws Exception {
//            request = new HttpRequest(new BufferedReader(new FileReader(testDirectory+"Hello.txt")));
//    }

    // 기본 요청 Test
    @Test
    public void parseGetRequestLine() throws Exception {
        HttpRequest request = new HttpRequest(new BufferedReader(new FileReader(testDirectory + "HTTP_Hello.txt")));
        MatcherAssert.assertThat(request.getMethod(), is(HttpMethod.GET));
        MatcherAssert.assertThat(request.getUri(), is("/Hello"));
        MatcherAssert.assertThat(request.getVersion(), is("HTTP/1.1"));
        MatcherAssert.assertThat(request.getHeaders().get("Host"), is("_default"));
    }

    // 설정파일에 정의해둔 host로 동작하는지 확인
    @Test
    public void virtualHost() throws Exception {
        HttpRequest request = new HttpRequest(new BufferedReader(new FileReader(testDirectory + "HTTP_VirtualHost.txt")));
        MatcherAssert.assertThat(request.getMethod(), is(HttpMethod.GET));
        MatcherAssert.assertThat(request.getVersion(), is("HTTP/1.1"));
        MatcherAssert.assertThat(request.getHeaders().get("Host"), is("client1.com"));
    }

    // 요청라인에 지원하지 않는 메서드 테스트
    @Test(expected = NotSupportedHttpMethodException.class)
    public void testNotImplemented() throws Exception {
        HttpRequest request = new HttpRequest(new BufferedReader(new FileReader(testDirectory + "HTTP_Method.txt")));
    }

    // 요청라인 메서드에 잘못된 메서드명으로 요청 테스트
    @Test(expected = IllegalArgumentException.class)
    public void test() throws Exception {
        HttpRequest request = new HttpRequest(new BufferedReader(new FileReader(testDirectory + "HTTP_MethodTypo.txt")));
    }

}
