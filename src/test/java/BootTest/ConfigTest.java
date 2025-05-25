package BootTest;

import com.was.config.ServerConfig;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.is;

public class ConfigTest {
    private String testDirectory = "src/test/resources/config/";

    @Test
    public void test() throws IOException {
        ServerConfig.load(Paths.get(testDirectory+"ServerConfig.json"));
        ServerConfig serverConfig = ServerConfig.getInstance();
        System.out.println(serverConfig.getPort());
        //MatcherAssert.assertThat(request.getHeaders().get("Host"), is("_default"));
    }
}
