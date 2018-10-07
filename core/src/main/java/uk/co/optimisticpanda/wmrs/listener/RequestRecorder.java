package uk.co.optimisticpanda.wmrs.listener;

import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.PostServeAction;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.google.common.collect.ImmutableMap;
import uk.co.optimisticpanda.wmrs.core.Entry;
import uk.co.optimisticpanda.wmrs.core.PerStubConfiguration;
import uk.co.optimisticpanda.wmrs.core.RequestStore;
import uk.co.optimisticpanda.wmrs.data.StoredEntry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.time.ZoneOffset.UTC;

public class RequestRecorder extends PostServeAction {

    public static final String EXTENSION_NAME = "mongo-request-recorder";
    private final RequestStore store;
    private FieldExtractor[] extractors;

    public RequestRecorder(final RequestStore store, final FieldExtractor... extractors) {
        this.store = store;
        this.extractors = extractors;
    }

    @Override
    public void doAction(final ServeEvent serveEvent, final Admin admin, final Parameters parameters) {

        PerStubConfiguration params = parameters.as(PerStubConfiguration.class);

        String storeName = params.getStoreName();
        List<String> tags = params.getTags();

        Entry entry = new StoredEntry(
                LocalDateTime.now(UTC),
                serveEvent.getRequest(),
                serveEvent.getResponseDefinition(),
                tags,
                extractFields(serveEvent, params));

        store.save(storeName, entry);
    }

    private Map<String, Object> extractFields(final ServeEvent serveEvent, final PerStubConfiguration params) {

        ImmutableMap.Builder<String, Object> fields = ImmutableMap.builder();

        for (FieldExtractor extractor : extractors) {
                fields.putAll(extractor.extract(params, serveEvent.getRequest()));
        }

        return fields.build();
    }


    @Override
    public String getName() {
        return EXTENSION_NAME;
    }
}
