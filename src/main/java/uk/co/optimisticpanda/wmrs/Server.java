package uk.co.optimisticpanda.wmrs;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import uk.co.optimisticpanda.wmrs.admin.StoreEndpoints;
import uk.co.optimisticpanda.wmrs.core.RequestStore;
import uk.co.optimisticpanda.wmrs.data.MongoRequestStore;
import uk.co.optimisticpanda.wmrs.listener.RequestRecorder;
import uk.co.optimisticpanda.wmrs.listener.extractors.BodyFieldExtractor;
import uk.co.optimisticpanda.wmrs.listener.extractors.UrlPathFieldExtractor;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class Server {

    public static void main(String[] args) {

        RequestStore store = new MongoRequestStore("mongodb://localhost:27017", "mock-server");

        WireMockServer server = new WireMockServer(options()
                .extensions(new RequestRecorder(store,
                        BodyFieldExtractor.INSTANCE,
                        UrlPathFieldExtractor.INSTANCE))
                .extensions(new StoreEndpoints(store))
                .fileSource(new ClasspathFileSource("requests/"))
                .port(8080));

        server.start();

    }



}
