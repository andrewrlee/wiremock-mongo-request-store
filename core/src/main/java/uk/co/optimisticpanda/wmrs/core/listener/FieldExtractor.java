package uk.co.optimisticpanda.wmrs.core.listener;

import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import uk.co.optimisticpanda.wmrs.core.PerStubConfiguration;

import java.util.Map;

public interface FieldExtractor {

    Map<String, Object> extract(PerStubConfiguration configuration, LoggedRequest request);

}
