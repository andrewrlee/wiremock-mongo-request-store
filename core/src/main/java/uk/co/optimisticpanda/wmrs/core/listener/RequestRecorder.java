package uk.co.optimisticpanda.wmrs.core.listener;

import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.PostServeAction;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.google.common.collect.ImmutableMap;
import uk.co.optimisticpanda.wmrs.core.EntryToStore;
import uk.co.optimisticpanda.wmrs.core.PerStubConfiguration;
import uk.co.optimisticpanda.wmrs.core.RequestStore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;

public class RequestRecorder extends PostServeAction {

    public static final String EXTENSION_NAME = "mongo-request-recorder";
    private final RequestStore store;
    private final FieldExtractor[] extractors;

    public RequestRecorder(final RequestStore store, final FieldExtractor... extractors) {
        this.store = store;
        this.extractors = extractors;
    }

    @Override
    public void doAction(final ServeEvent serveEvent, final Admin admin, final Parameters parameters) {

        PerStubConfiguration params = parameters.as(PerStubConfiguration.class);

        String storeName = params.getStoreName();
        List<String> tags = params.getTags();

        EntryToStore entry = EntryToStore.builder()
                .withId(UUID.randomUUID())
                .withTimestamp(LocalDateTime.now(UTC))
                .withRequest(serveEvent.getRequest())
                .withResponse(serveEvent.getResponseDefinition())
                .withTags(tags)
                .withFields(extractFields(serveEvent, params))
                .build();

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
