package uk.co.optimisticpanda.wmrs.admin;

import com.github.tomakehurst.wiremock.admin.AdminTask;
import com.github.tomakehurst.wiremock.admin.model.PathParams;
import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import uk.co.optimisticpanda.wmrs.admin.model.SearchFields;

public class SearchFieldsController implements AdminTask {

    static String PATH = "/store/fields";

    @Override
    public ResponseDefinition execute(Admin admin, Request request, PathParams pathParams) {
        return ResponseDefinition.okForJson(new SearchFields(admin));
    }
}