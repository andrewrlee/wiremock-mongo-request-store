package uk.co.optimisticpanda.wmrs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.*;

public class RequestRecorderParams {

    @JsonProperty("collection-name")
    private String collectionName;

    @JsonProperty("tags")
    private List<String> tags = new ArrayList<>();

    @JsonProperty("fieldExtractors")
    private Map<String, Map<String, String>> fieldExtractors = new HashMap<>();

    public static List<RequestRecorderParams> fromAdmin(Admin admin) {
        List<StubMapping> mappings = admin.listAllStubMappings().getMappings();

        return mappings.stream()
                .flatMap(m -> m.getPostServeActions().entrySet().stream())
                .filter(e -> e.getKey().equals("mongo-request-recorder"))
                .map(e -> e.getValue().as(RequestRecorderParams.class))
                .collect(toList());
    }

    public String getCollectionName() {
        return requireNonNull(collectionName, "collection name must not be null");
    }

    public List<String> getTags() {
        return tags;
    }

    public Map<String, String> getPathExtractors() {
        return fieldExtractors.getOrDefault("path", emptyMap());
    }

    public Map<String, String> getBodyExtractors() {
        return fieldExtractors.getOrDefault("body", emptyMap());
    }

    public Set<String> getFields() {
        return ImmutableSet.<String>builder()
                .addAll(getBodyExtractors().keySet())
                .addAll(getPathExtractors().keySet())
                .build();
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("collectionName", collectionName)
                .add("tags", tags)
                .add("fieldExtractors", fieldExtractors)
                .toString();
    }
}
