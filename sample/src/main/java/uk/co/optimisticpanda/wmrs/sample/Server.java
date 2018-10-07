package uk.co.optimisticpanda.wmrs.sample;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.SingleRootFileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.optimisticpanda.wmrs.core.admin.StoreEndpoints;
import uk.co.optimisticpanda.wmrs.core.RequestStore;
import uk.co.optimisticpanda.wmrs.mongo3_6.MongoRequestStore;
import uk.co.optimisticpanda.wmrs.core.listener.RequestRecorder;
import uk.co.optimisticpanda.wmrs.core.listener.extractors.BodyFieldExtractor;
import uk.co.optimisticpanda.wmrs.core.listener.extractors.UrlPathFieldExtractor;

import java.io.File;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

public class Server {

    private static final Logger L = LoggerFactory.getLogger(Server.class);

    public static void main(final String[] args) throws Exception {

        if (args.length == 0) {
            String jarname = new java.io.File(Server.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath())
                    .getName();

            L.error(format("usage: java -jar %s </path/to/mappings>", jarname));
            return;
        }

        SingleRootFileSource rootFileSource = new SingleRootFileSource(asExistingDir(args[0]));

        RequestStore store = new MongoRequestStore("mongodb://localhost:27017", "mock-server");

        WireMockServer server = new WireMockServer(options()
                .extensions(new RequestRecorder(store,
                        BodyFieldExtractor.INSTANCE,
                        UrlPathFieldExtractor.INSTANCE))
                .extensions(new StoreEndpoints(store))
                .fileSource(rootFileSource)
                .port(8080));

        server.start();

    }

    private static File asExistingDir(final String fileName) {
        File file = new File(fileName);
        checkState(file.exists(), "'%s' should exist");
        checkState(file.isDirectory(), "'%s' should be a directory");
        return file;
    }
}
