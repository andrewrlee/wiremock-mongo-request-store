package uk.co.optimisticpanda.wmrs.admin;

import com.github.tomakehurst.wiremock.admin.AdminTask;
import com.github.tomakehurst.wiremock.admin.model.PathParams;
import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import uk.co.optimisticpanda.wmrs.admin.model.Links;
import uk.co.optimisticpanda.wmrs.admin.model.PerStubConfigurationsExtractor;
import uk.co.optimisticpanda.wmrs.admin.model.Results;
import uk.co.optimisticpanda.wmrs.core.PerStubConfigurations;
import uk.co.optimisticpanda.wmrs.core.RequestQuery;
import uk.co.optimisticpanda.wmrs.core.RequestStore;

import static uk.co.optimisticpanda.wmrs.admin.model.QueryParameters.extractMatchingParams;
import static uk.co.optimisticpanda.wmrs.admin.model.QueryParameters.limit;
import static uk.co.optimisticpanda.wmrs.admin.model.QueryParameters.offset;
import static uk.co.optimisticpanda.wmrs.admin.model.QueryParameters.since;

public class EntriesByTagController implements AdminTask {

    static String PATH = "/store/{store-name}/entries/tag/{tag}";

    private final RequestStore requestStore;

    public EntriesByTagController(final RequestStore requestStore) {
        this.requestStore = requestStore;
    }

    @Override
    public ResponseDefinition execute(final Admin admin, final Request request, final PathParams pathParams) {

        PerStubConfigurations configurations = PerStubConfigurationsExtractor.fromAdmin(admin);
        String tag = pathParams.get("tag");

        RequestQuery query = RequestQuery.forStore(pathParams.get("store-name"))
                .withTag(tag)
                .withFieldsToMatch(extractMatchingParams(request, configurations.searchFieldsForTag(tag)))
                .withLimit(limit(request).orElse(null))
                .withSince(since(request).orElse(null))
                .withOffset(offset(request).orElse(null))
                .build();

        return ResponseDefinition.okForJson(new Results(requestStore.query(query), Links.create(request)));
    }
}