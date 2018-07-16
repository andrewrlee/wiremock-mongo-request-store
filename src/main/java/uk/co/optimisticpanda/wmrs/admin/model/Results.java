package uk.co.optimisticpanda.wmrs.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.optimisticpanda.wmrs.core.Entry;
import uk.co.optimisticpanda.wmrs.data.StoredEntry;

import java.util.List;

public class Results {
    @JsonProperty
    private List<? extends Entry> entries;

    public Results(final List<? extends Entry> entries) {
        this.entries = entries;
    }
}
