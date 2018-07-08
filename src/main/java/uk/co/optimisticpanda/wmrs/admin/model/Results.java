package uk.co.optimisticpanda.wmrs.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.optimisticpanda.wmrs.data.Entry;

import java.util.List;

public class Results {
    @JsonProperty
    private List<Entry> entries;

    public Results(List<Entry> entries) {
        this.entries = entries;
    }
}
