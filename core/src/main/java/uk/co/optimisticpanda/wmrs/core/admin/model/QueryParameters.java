package uk.co.optimisticpanda.wmrs.core.admin.model;

import com.github.tomakehurst.wiremock.http.QueryParameter;
import com.github.tomakehurst.wiremock.http.Request;

import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.stream.Collectors.toMap;

public class QueryParameters {

    private QueryParameters() {
    }

    public static Optional<Integer> limit(final Request request) {
        return firstQueryParam(request, "limit")
            .map(Integer::valueOf);
    }

    public static Optional<Integer> offset(final Request request) {
        return firstQueryParam(request, "offset")
                .map(Integer::valueOf);
    }

    public static Optional<LocalDateTime> since(final Request request) {
        return firstQueryParam(request, "since")
                .map(val -> LocalDateTime.parse(val, ISO_DATE_TIME));
    }

    public static Map<String, String> extractMatchingParams(final Request request,
                                                            final Collection<String> paramNames) {
        return paramNames.stream()
                .map(request::queryParameter)
                .filter(QueryParameter::isPresent)
                .map(param -> new SimpleEntry<>(param.key(), param.firstValue()))
                .collect(toMap(Entry::getKey, Entry::getValue));
    }

    private static Optional<String> firstQueryParam(final Request request, final String key) {
        QueryParameter parameter = request.queryParameter(key);
        return parameter.isPresent() ? parameter.values().stream().findFirst() : Optional.empty();
    }

}
