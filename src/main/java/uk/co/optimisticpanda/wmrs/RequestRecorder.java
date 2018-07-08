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
import uk.co.optimisticpanda.wmrs.data.Entry;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.ZoneOffset.UTC;
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
                LocalDateTime.now(UTC),
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

        String path = Urls.decode(request.getUrl());

        Map<String, Object> fields = new HashMap<>();
        for (Map.Entry<String, String> entry : pathExtractors.entrySet()) {
            Matcher matcher = Pattern.compile(entry.getValue()).matcher(path);
            if (matcher.find()) {
                fields.put(entry.getKey(), matcher.group(1));
            }
        }
        return fields;
    }

    @Override
    public String getName() {
        return "mongo-request-recorder";
    }
}
