package uk.co.optimisticpanda.wmrs.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import uk.co.optimisticpanda.wmrs.core.Entry;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static uk.co.optimisticpanda.wmrs.admin.model.Links.Link;

public class EntryWithLinks implements Entry {

    @JsonUnwrapped
    private Entry entry;
    @JsonProperty
    private Map<String, String> links = new LinkedHashMap<>();

    public EntryWithLinks(final Entry entry, final List<Link> links) {
        this.entry = entry;
        links.forEach(link -> this.links.put(link.getRel(), link.getHref()));
    }

    @JsonIgnore
    @Override
    public String getId() {
        return entry.getId();
    }

    @JsonIgnore
    @Override
    public Set<String> getTags() {
        return entry.getTags();
    }

    @JsonIgnore
    @Override
    public LocalDateTime getTimestamp() {
        return entry.getTimestamp();
    }

    @JsonIgnore
    @Override
    public Map<String, Object> getFields() {
        return entry.getFields();
    }
}
