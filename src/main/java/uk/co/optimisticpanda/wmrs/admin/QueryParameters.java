package uk.co.optimisticpanda.wmrs.admin;

import com.github.tomakehurst.wiremock.http.QueryParameter;
import com.github.tomakehurst.wiremock.http.Request;

import java.time.LocalDateTime;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.stream.Collectors.toMap;

class QueryParameters {

    private static Optional<String> firstQueryParam(Request request, String key) {
        QueryParameter parameter = request.queryParameter(key);
        return parameter.isPresent() ? parameter.values().stream().findFirst() : Optional.empty();
    }

    static Optional<Integer> limit(Request request) {
        return firstQueryParam(request, "limit")
            .map(Integer::valueOf);
    }

    static Optional<LocalDateTime> since(Request request) {
        return firstQueryParam(request, "since")
                .map(val -> LocalDateTime.parse(val, ISO_DATE_TIME));
    }

    static Map<String, String> extractMatchingParams(Request request, Collection<String> paramNames) {
        return paramNames.stream()
                .map(request::queryParameter)
                .filter(QueryParameter::isPresent)
                .map(param -> new SimpleEntry<>(param.key(), param.firstValue()))
                .collect(toMap(Entry::getKey, Entry::getValue));
    }
}
