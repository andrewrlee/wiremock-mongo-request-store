package uk.co.optimisticpanda.wmrs.core;

import com.google.common.base.MoreObjects;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import static com.google.common.base.MoreObjects.toStringHelper;

public class RequestQuery {

    private final String storeName;
    private String tag;
    private Map<String, String> fieldsToMatch = new TreeMap<>();
    private Integer limit;
    private LocalDateTime since;

    private RequestQuery(Builder builder) {
        storeName = builder.storeName;
        tag = builder.tag;
        fieldsToMatch.putAll(builder.fieldsToMatch);
        limit = builder.limit;
        since = builder.since;
    }

    public static Builder forStore(String storeName) {
        return new Builder(storeName);
    }

    public String getStoreName() {
        return storeName;
    }

    public Optional<String> getTag() {
        return Optional.ofNullable(tag);
    }

    public Map<String, String> getFieldsToMatch() {
        return fieldsToMatch;
    }

    public Optional<Integer> getLimit() {
        return Optional.ofNullable(limit);
    }

    public Optional<LocalDateTime> getSince() {
        return Optional.ofNullable(since);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestQuery that = (RequestQuery) o;
        return Objects.equals(storeName, that.storeName) &&
                Objects.equals(tag, that.tag) &&
                Objects.equals(fieldsToMatch, that.fieldsToMatch) &&
                Objects.equals(limit, that.limit) &&
                Objects.equals(since, that.since);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeName, tag, fieldsToMatch, limit, since);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("storeName", storeName)
                .add("tag", tag)
                .add("fieldsToMatch", fieldsToMatch)
                .add("limit", limit)
                .add("since", since)
                .toString();
    }

    public static final class Builder {
        private final String storeName;
        private String tag;
        private Map<String, String> fieldsToMatch = new TreeMap<>();
        private Integer limit;
        private LocalDateTime since;

        private Builder(String storeName) {
            this.storeName = storeName;
        }

        public Builder withTag(String val) {
            tag = val;
            return this;
        }

        public Builder withFieldsToMatch(Map<String, String> val) {
            fieldsToMatch = val;
            return this;
        }

        public Builder withLimit(Integer val) {
            limit = val;
            return this;
        }

        public Builder withSince(LocalDateTime val) {
            since = val;
            return this;
        }

        public RequestQuery build() {
            return new RequestQuery(this);
        }
    }
}
