package uk.co.optimisticpanda.wmrs.core;

import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.google.common.collect.ImmutableSet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.MoreObjects.toStringHelper;

public class EntryToStore {

    private final UUID id;
    private final LocalDateTime timestamp;
    private final Request request;
    private final ResponseDefinition response;
    private final List<String> tags;
    private final Map<String, Object> fields;

    private EntryToStore(Builder builder) {
        id = builder.id;
        timestamp = builder.timestamp;
        request = builder.request;
        response = builder.response;
        tags = builder.tags;
        fields = builder.fields;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public Set<String> getTags() {
        return ImmutableSet.copyOf(tags);
    }

    public Request getRequest() {
        return request;
    }

    public ResponseDefinition getResponse() {
        return response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntryToStore that = (EntryToStore) o;
        return Objects.equals(id, that.id)
                && Objects.equals(timestamp, that.timestamp)
                && Objects.equals(request, that.request)
                && Objects.equals(response, that.response)
                && Objects.equals(tags, that.tags)
                && Objects.equals(fields, that.fields);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("id", id)
                .add("timestamp", timestamp)
                .add("request", request)
                .add("response", response)
                .add("tags", tags)
                .add("fields", fields)
                .toString();
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, timestamp, request, response, tags, fields);
    }

    public static final class Builder {
        private UUID id;
        private LocalDateTime timestamp;
        private Request request;
        private ResponseDefinition response;
        private List<String> tags = new ArrayList<>();
        private Map<String, Object> fields = new LinkedHashMap<>();

        private Builder() {
        }

        public Builder withId(final UUID val) {
            id = val;
            return this;
        }

        public Builder withTimestamp(final LocalDateTime val) {
            timestamp = val;
            return this;
        }

        public Builder withRequest(final Request val) {
            request = val;
            return this;
        }

        public Builder withResponse(final ResponseDefinition val) {
            response = val;
            return this;
        }

        public Builder withTags(final List<String> val) {
            tags.addAll(val);
            return this;
        }


        public Builder withFields(final Map<String, Object> val) {
            fields.putAll(val);
            return this;
        }

        public EntryToStore build() {
            return new EntryToStore(this);
        }
    }
}
