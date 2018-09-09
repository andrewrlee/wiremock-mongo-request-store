package uk.co.optimisticpanda.wmrs.admin;

import com.github.tomakehurst.wiremock.admin.AdminTask;
import com.github.tomakehurst.wiremock.admin.model.PathParams;
import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import uk.co.optimisticpanda.wmrs.admin.model.EntryWithLinks;
import uk.co.optimisticpanda.wmrs.admin.model.Links;
import uk.co.optimisticpanda.wmrs.admin.model.Results;
import uk.co.optimisticpanda.wmrs.admin.model.PerStubConfigurationsExtractor;
import uk.co.optimisticpanda.wmrs.core.Entry;
import uk.co.optimisticpanda.wmrs.core.PerStubConfigurations;
import uk.co.optimisticpanda.wmrs.core.ListQuery;
import uk.co.optimisticpanda.wmrs.core.RequestStore;

import java.util.List;

import static java.util.stream.Collectors.*;
import static uk.co.optimisticpanda.wmrs.admin.model.QueryParameters.extractMatchingParams;
import static uk.co.optimisticpanda.wmrs.admin.model.QueryParameters.limit;
import static uk.co.optimisticpanda.wmrs.admin.model.QueryParameters.offset;
import static uk.co.optimisticpanda.wmrs.admin.model.QueryParameters.since;

public class AllEntriesController implements AdminTask {

    static String PATH = "/store/{store-name}/entries/";

    private final RequestStore requestStore;

    public AllEntriesController(final RequestStore requestStore) {
        this.requestStore = requestStore;
    }

    @Override
    public ResponseDefinition execute(final Admin admin, final Request request, final PathParams pathParams) {

        PerStubConfigurations configurations = PerStubConfigurationsExtractor.fromAdmin(admin);

        ListQuery query = ListQuery.forStore(pathParams.get("store-name"))
                .withFieldsToMatch(extractMatchingParams(request, configurations.allSearchFields()))
                .withLimit(limit(request).orElse(null))
                .withSince(since(request).orElse(null))
                .withOffset(offset(request).orElse(null))
                .build();

        List<? extends Entry> entries = requestStore.query(query);
        List<? extends Entry> result = entries.stream().map(this::withLinks).collect(toList());

        return ResponseDefinition.okForJson(new Results(result, Links.forPage(request)));
    }

    private Entry withLinks(final Entry e) {
        return new EntryWithLinks(e, Links.forEntry(e.getId()));
    }
}