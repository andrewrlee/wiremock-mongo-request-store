package uk.co.optimisticpanda.wmrs.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;

public class PerStubConfiguration {

    @JsonProperty("collection-name")
    private String collectionName;

    @JsonProperty("tags")
    private List<String> tags = new ArrayList<>();

    @JsonProperty("fieldExtractors")
    private Map<String, Map<String, String>> fieldExtractors = new HashMap<>();

    private PerStubConfiguration(Builder builder) {
        collectionName = builder.collectionName;
        tags = builder.tags;
        fieldExtractors = builder.fieldExtractors;
    }


    private PerStubConfiguration() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getStoreName() {
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

    public static final class Builder {
        private String collectionName;
        private final List<String> tags = new ArrayList<>();
        private final Map<String, Map<String, String>> fieldExtractors = new HashMap<>();

        private Builder() {
        }

        public Builder withCollectionName(final String val) {
            collectionName = val;
            return this;
        }

        public Builder withTags(final String... vals) {
            tags.addAll(asList(vals));
            return this;
        }

        public Builder withPathExtractors(final Map<String, String> val) {
            fieldExtractors.put("path", val);
            return this;
        }

        public Builder withBodyExtractors(final Map<String, String> val) {
            fieldExtractors.put("body", val);
            return this;
        }

        public PerStubConfiguration build() {
            return new PerStubConfiguration(this);
        }
    }
}
