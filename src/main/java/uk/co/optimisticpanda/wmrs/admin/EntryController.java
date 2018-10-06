package uk.co.optimisticpanda.wmrs.admin;

import com.github.tomakehurst.wiremock.admin.AdminTask;
import com.github.tomakehurst.wiremock.admin.model.PathParams;
import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import uk.co.optimisticpanda.wmrs.core.EntryQuery;
import uk.co.optimisticpanda.wmrs.core.RequestStore;

public class EntryController implements AdminTask {

    static String PATH = "/store/{store-name}/entries/{entry}";

    private final RequestStore requestStore;

    public EntryController(final RequestStore requestStore) {
        this.requestStore = requestStore;
    }

    @Override
    public ResponseDefinition execute(final Admin admin, final Request request, final PathParams pathParams) {

        EntryQuery query = EntryQuery.forStore(pathParams.get("store-name"))
                .withId(pathParams.get("entry"))
                .build();

        return ResponseDefinition.okForJson(requestStore.query(query));
    }
}