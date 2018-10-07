package uk.co.optimisticpanda.wmrs.core;

import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

public class EntryQuery {

    private final String storeName;
    private final String id;

    private EntryQuery(final Builder builder) {
        storeName = builder.storeName;
        id = builder.id;
    }

    public static Builder forStore(final String storeName) {
        return new Builder(storeName);
    }

    public String getStoreName() {
        return storeName;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntryQuery that = (EntryQuery) o;
        return Objects.equals(storeName, that.storeName)
                && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeName, id);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("storeName", storeName)
                .add("id", id)
                .toString();
    }

    public static final class Builder {
        private final String storeName;
        private String id;

        private Builder(final String storeName) {
            this.storeName = storeName;
        }

        public Builder withId(final String val) {
            id = val;
            return this;
        }

        public EntryQuery build() {
            return new EntryQuery(this);
        }
    }
}
