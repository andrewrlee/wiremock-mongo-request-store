package uk.co.optimisticpanda.wmrs.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.optimisticpanda.wmrs.admin.model.Links.Link;
import uk.co.optimisticpanda.wmrs.core.Entry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Results {
    @JsonProperty
    private List<? extends Entry> entries;

    @JsonProperty
    private Map<String, String> links = new LinkedHashMap<>();

    public Results(
            final List<? extends Entry> entries,
            final List<? extends Link> links) {
        this.entries = entries;
        links.forEach(link -> this.links.put(link.getRel(), link.getHref()));
    }
}
