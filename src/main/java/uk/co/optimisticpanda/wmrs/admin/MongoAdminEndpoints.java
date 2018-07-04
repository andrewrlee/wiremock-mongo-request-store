package uk.co.optimisticpanda.wmrs.admin;

import com.github.tomakehurst.wiremock.admin.Router;
import com.github.tomakehurst.wiremock.extension.AdminApiExtension;
import com.github.tomakehurst.wiremock.http.RequestMethod;

public class MongoAdminEndpoints implements AdminApiExtension {

    @Override
    public void contributeAdminApiRoutes(Router router) {

        router.add(RequestMethod.GET, "/tags", new ListTags());
        router.add(RequestMethod.GET, "/fields", new ListFields());
    }

    @Override
    public String getName() {
        return "mongo-endpoints";
    }
}
