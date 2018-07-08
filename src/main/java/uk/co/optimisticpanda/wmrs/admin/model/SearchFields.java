package uk.co.optimisticpanda.wmrs.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.core.Admin;
import uk.co.optimisticpanda.wmrs.RequestRecorderParams;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public class SearchFields {

    @JsonProperty("fields")
    private Map<String, Set<String>> fields = new HashMap<>();

    public SearchFields() {
    }

    public SearchFields(Admin admin) {
        List<RequestRecorderParams> params = RequestRecorderParams.fromAdmin(admin);
        this.fields = params.stream().flatMap(this::toEntries).collect(toMap(Entry::getKey, Entry::getValue));
    }

    public SearchFields(Admin admin, String tag) {
        List<RequestRecorderParams> params = RequestRecorderParams.fromAdmin(admin);
        this.fields = params.stream()
                .filter(param -> param.getTags().contains(tag))
                .flatMap(this::toEntries).collect(toMap(Entry::getKey, Entry::getValue));
    }

    public static Set<String> all(Admin admin) {
        return new SearchFields(admin).getFields();
    }

    public static Set<String> fieldsForTag(Admin admin, String tag) {
        return new SearchFields(admin, tag).getFields();
    }

    private Stream<Entry<String, Set<String>>> toEntries(RequestRecorderParams param) {
        return param.getTags().stream().map(t -> new SimpleEntry<>(t, param.getFields()));
    }

    public Map<String, Set<String>> getTagToFields() {
        return fields;
    }

    public Set<String> getFields() {
        return fields.values().stream().flatMap(Collection::stream).collect(toSet());
    }
}
