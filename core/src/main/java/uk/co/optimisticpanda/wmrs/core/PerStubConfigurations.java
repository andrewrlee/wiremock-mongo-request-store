package uk.co.optimisticpanda.wmrs.core;

import com.google.common.collect.ImmutableSet;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public class PerStubConfigurations {

    private final List<PerStubConfiguration> requestRecorderParams;

    public PerStubConfigurations(final List<PerStubConfiguration> requestRecorderParams) {
        this.requestRecorderParams = requestRecorderParams;
    }

    public Set<String> allSearchFields() {
        return requestRecorderParams.stream()
                .flatMap(e -> e.getFields().stream())
                .collect(toSet());
    }

    public Set<String> searchFieldsForTag(String tag) {
        return requestRecorderParams.stream()
                .filter(param -> param.getTags().contains(tag))
                .flatMap(e -> e.getFields().stream())
                .collect(toSet());
    }

    public Map<String, Map<String, Set<String>>> searchFieldsByStoreAndTag() {

        Map<String, List<PerStubConfiguration>> paramsByStore = requestRecorderParams.stream()
                .collect(groupingBy(PerStubConfiguration::getStoreName));

        return paramsByStore.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> extractFieldsByTags(e.getValue())));
    }

    private Map<String, Set<String>> extractFieldsByTags(final List<PerStubConfiguration> params) {

        return params.stream()
                .flatMap(this::tagToFields)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue
                        , (s1, s2) ->
                        ImmutableSet.<String>builder()
                                .addAll(s1)
                                .addAll(s2).build()));
    }

    private Stream<Map.Entry<String, Set<String>>> tagToFields(final PerStubConfiguration paramsByFields) {
        return paramsByFields.getTags().stream().map(t -> new SimpleEntry<>(t, paramsByFields.getFields()));
    }

}
