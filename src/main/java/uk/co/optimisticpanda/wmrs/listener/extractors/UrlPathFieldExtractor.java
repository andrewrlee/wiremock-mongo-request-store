package uk.co.optimisticpanda.wmrs.listener.extractors;

import com.github.tomakehurst.wiremock.common.Urls;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import uk.co.optimisticpanda.wmrs.core.PerStubConfiguration;
import uk.co.optimisticpanda.wmrs.listener.FieldExtractor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum UrlPathFieldExtractor implements FieldExtractor  {
    INSTANCE;

    @Override
    public Map<String, Object> extract(final PerStubConfiguration configuration, final LoggedRequest request) {

        String path = Urls.decode(request.getUrl());

        Map<String, Object> fields = new HashMap<>();
        for (Entry<String, String> entry : configuration.getPathExtractors().entrySet()) {
            Matcher matcher = Pattern.compile(entry.getValue()).matcher(path);
            if (matcher.find()) {
                fields.put(entry.getKey(), matcher.group(1));
            }
        }
        return fields;
    }
}
