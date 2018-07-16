package uk.co.optimisticpanda.wmrs.admin.model;

import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import uk.co.optimisticpanda.wmrs.core.PerStubConfiguration;
import uk.co.optimisticpanda.wmrs.core.PerStubConfigurations;
import uk.co.optimisticpanda.wmrs.listener.RequestRecorder;

import java.util.List;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class PerStubConfigurationsExtractor {

    private PerStubConfigurationsExtractor() {
    }

    public static PerStubConfigurations fromAdmin(final Admin admin) {

        List<StubMapping> mappings = admin.listAllStubMappings().getMappings();

        return mappings.stream()
                .filter(m -> m.getPostServeActions() != null)
                .flatMap(m -> m.getPostServeActions().entrySet().stream())
                .filter(e -> e.getKey().equals(RequestRecorder.EXTENSION_NAME))
                .map(e -> e.getValue().as(PerStubConfiguration.class))
                .collect(collectingAndThen(toList(), PerStubConfigurations::new));
    }
}
