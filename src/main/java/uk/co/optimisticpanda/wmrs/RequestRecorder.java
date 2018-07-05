package uk.co.optimisticpanda.wmrs;

import com.github.tomakehurst.wiremock.common.Urls;
import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.PostServeAction;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.JsonPath;
import uk.co.optimisticpanda.wmrs.core.RequestStore;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class RequestRecorder extends PostServeAction {

    private final RequestStore store;

    public RequestRecorder(RequestStore store) {
        this.store = store;
    }

    @Override
    public void doAction(ServeEvent serveEvent, Admin admin, Parameters parameters) {

        RequestRecorderParams params = parameters.as(RequestRecorderParams.class);

        String storeName = params.getCollectionName();
        List<String> tags = params.getTags();

        Map<String, Object> fields = ImmutableMap.<String, Object>builder()
                .putAll(bodyFields(serveEvent.getRequest(), params.getBodyExtractors()))
                .putAll(pathFields(serveEvent.getRequest(), params.getPathExtractors()))
                .build();

        Entry entry = new Entry(
                serveEvent.getRequest(),
                serveEvent.getResponseDefinition(),
                tags,
                fields);

        store.save(storeName, entry);
    }

    private Map<String, Object> bodyFields(LoggedRequest request, Map<String, String> bodyExtractors) {

        String body = request.getBodyAsString();

        return bodyExtractors.entrySet().stream().collect(toMap(
                Map.Entry::getKey,
                e -> JsonPath.read(body, e.getValue())));
    }

    private Map<String, Object> pathFields(LoggedRequest request, Map<String, String> pathExtractors) {

        String path = request.getUrl();

        return pathExtractors.entrySet().stream().collect(toMap(
                Map.Entry::getKey,
                e -> Urls.decode(path.replaceFirst(e.getValue(), "$1"))));
    }

    @Override
    public String getName() {
        return "mongo-request-recorder";
    }
}
