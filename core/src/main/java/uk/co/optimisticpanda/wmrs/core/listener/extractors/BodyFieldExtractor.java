package uk.co.optimisticpanda.wmrs.core.listener.extractors;

import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.jayway.jsonpath.JsonPath;
import uk.co.optimisticpanda.wmrs.core.PerStubConfiguration;
import uk.co.optimisticpanda.wmrs.core.listener.FieldExtractor;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

public enum BodyFieldExtractor implements FieldExtractor  {
    INSTANCE;

    @Override
    public Map<String, Object> extract(final PerStubConfiguration configuration, final LoggedRequest request) {

        String body = request.getBodyAsString();

        return configuration.getBodyExtractors().entrySet().stream().collect(toMap(
                Map.Entry::getKey,
                e -> JsonPath.read(body, e.getValue())));
    }
}
