package uk.co.optimisticpanda.wmrs;

import com.github.tomakehurst.wiremock.common.Metadata;
import com.github.tomakehurst.wiremock.common.Urls;
import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.PostServeAction;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.JsonPath;
import uk.co.optimisticpanda.wmrs.core.RequestStore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.common.Metadata.metadata;
import static java.util.stream.Collectors.toList;

public class RequestRecorder extends PostServeAction {

    private final RequestStore store;

    public RequestRecorder(RequestStore store) {
        this.store = store;
    }

    @Override
    public void doAction(ServeEvent serveEvent, Admin admin, Parameters parameters) {

        String storeName = parameters.getString("collection-name");
        List<String> tags = parameters.getList("tags").stream().map(String::valueOf).collect(toList());

        Metadata extractors = safeGet(parameters, "fieldExtractors");

        Map<String, Object> fields = ImmutableMap.<String, Object>builder()
                .putAll(bodyFields(serveEvent.getRequest(), extractors))
                .putAll(pathFields(serveEvent.getRequest(), extractors))
                .build();

        Entry entry = new Entry(
                serveEvent.getRequest(),
                serveEvent.getResponseDefinition(),
                tags,
                fields);

        store.save(storeName, entry);
    }

    private Map<String, Object> pathFields(LoggedRequest request, Metadata extractors) {

        Metadata fields = safeGet(extractors, "path");

        Map<String, Object> data =  new LinkedHashMap<>();
        String path = request.getUrl();

        fields.forEach((key, value) -> {
            String extractedValue = path.replaceFirst(String.valueOf(value), "$1");
            data.put(key, Urls.decode(extractedValue));
        });

        return data;
    }


    private Map<String, Object> bodyFields(Request request, Metadata extractors) {

        Metadata fields = safeGet(extractors, "body");

        String bodyAsString = request.getBodyAsString();

        Map<String, Object> data =  new LinkedHashMap<>();

        fields.forEach((key, value) -> {
            Object result = JsonPath.read(bodyAsString, value.toString());
            data.put(key, result);
        });

        return data;
    }

    private Metadata safeGet(Metadata metadata, String name) {
        return metadata.containsKey(name)
                ? metadata.getMetadata(name)
                : metadata().build();
    }

    @Override
    public String getName() {
        return "mongo-request-recorder";
    }
}
