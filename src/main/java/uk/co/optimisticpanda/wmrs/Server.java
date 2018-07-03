package uk.co.optimisticpanda.wmrs;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import uk.co.optimisticpanda.wmrs.data.MongoRequestStore;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class Server {

    public static void main(String[] args) {

        WireMockServer server = new WireMockServer(options()
                .extensions(new RequestRecorder(new MongoRequestStore("mongodb://localhost:27017", "mock-server")))
                .fileSource(new ClasspathFileSource("requests/"))
                .port(8080));

        server.start();

    }



}
