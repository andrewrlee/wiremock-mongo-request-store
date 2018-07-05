package uk.co.optimisticpanda.wmrs.admin;

import com.github.tomakehurst.wiremock.admin.AdminTask;
import com.github.tomakehurst.wiremock.admin.model.PathParams;
import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import uk.co.optimisticpanda.wmrs.RequestRecorderParams;

import java.util.List;

import static java.util.stream.Collectors.toSet;

public class ListFields implements AdminTask {
    @Override
    public ResponseDefinition execute(Admin admin, Request request, PathParams pathParams) {
        List<StubMapping> mappings = admin.listAllStubMappings().getMappings();

        Object tags = mappings.stream()
                .flatMap(m -> m.getPostServeActions().entrySet().stream())
                .filter(e -> e.getKey().equals("mongo-request-recorder"))
                .map(e -> e.getValue().as(RequestRecorderParams.class))
                .flatMap(params -> params.getFields().stream())
                .collect(toSet());

        return ResponseDefinition.okForJson(tags);
    }
}