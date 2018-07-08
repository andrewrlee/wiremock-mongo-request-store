package uk.co.optimisticpanda.wmrs.admin;

import com.github.tomakehurst.wiremock.admin.AdminTask;
import com.github.tomakehurst.wiremock.admin.model.PathParams;
import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import uk.co.optimisticpanda.wmrs.admin.model.Results;
import uk.co.optimisticpanda.wmrs.admin.model.SearchFields;
import uk.co.optimisticpanda.wmrs.core.RequestQuery;
import uk.co.optimisticpanda.wmrs.core.RequestStore;

import static uk.co.optimisticpanda.wmrs.admin.QueryParameters.*;

public class EntriesByTagController implements AdminTask {

    static String PATH = "/store/{store-name}/entries/tag/{tag}";

    private final RequestStore requestStore;

    public EntriesByTagController(RequestStore requestStore) {
        this.requestStore = requestStore;
    }

    @Override
    public ResponseDefinition execute(Admin admin, Request request, PathParams pathParams) {

        RequestQuery query = RequestQuery.forStore(pathParams.get("store-name"))
                .withTag(pathParams.get("tag"))
                .withFieldsToMatch(extractMatchingParams(request, SearchFields.all(admin)))
                .withLimit(limit(request).orElse(null))
                .withSince(since(request).orElse(null))
                .build();

        return ResponseDefinition.okForJson(new Results(requestStore.query(query)));
    }
}