package uk.co.optimisticpanda.wmrs.admin.model;

import com.github.tomakehurst.wiremock.core.Admin;
import uk.co.optimisticpanda.wmrs.RequestRecorderParams;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class SearchFields {


    public static Set<String> all(Admin admin) {
        return RequestRecorderParams.fromAdmin(admin).stream()
                .flatMap(e -> e.getFields().stream())
                .collect(toSet());
    }

    public static Set<String> fieldsForTag(Admin admin, String tag) {
        return RequestRecorderParams.fromAdmin(admin).stream()
                .filter(param -> param.getTags().contains(tag))
                .flatMap(e -> e.getFields().stream())
                .collect(toSet());
    }

    public static Map<String, Map<String, Set<String>>> byStoreAndTag(Admin admin) {

        Map<String, List<RequestRecorderParams>> paramsByStore = RequestRecorderParams.fromAdmin(admin).stream()
                .collect(groupingBy(RequestRecorderParams::getStoreName));

        return paramsByStore.entrySet().stream()
                .map(e -> new SimpleEntry<>(e.getKey(), asMap(e.getValue())))
                .collect(toMap(Entry::getKey, Entry::getValue));
    }

    private static Map<String, Set<String>> asMap(List<RequestRecorderParams> params) {
        return params.stream().flatMap(SearchFields::toEntries).collect(toMap(Entry::getKey, Entry::getValue));
    }

    private static Stream<Entry<String, Set<String>>> toEntries(RequestRecorderParams param) {
        return param.getTags().stream().map(t -> new SimpleEntry<>(t, param.getFields()));
    }
}
