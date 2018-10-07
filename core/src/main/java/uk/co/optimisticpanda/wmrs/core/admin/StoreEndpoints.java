package uk.co.optimisticpanda.wmrs.core.admin;

import com.github.tomakehurst.wiremock.admin.Router;
import com.github.tomakehurst.wiremock.extension.AdminApiExtension;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import uk.co.optimisticpanda.wmrs.core.RequestStore;

public class StoreEndpoints implements AdminApiExtension {

    private final RequestStore requestStore;

    public StoreEndpoints(RequestStore requestStore) {
        this.requestStore = requestStore;
    }

    @Override
    public void contributeAdminApiRoutes(Router router) {

        router.add(RequestMethod.GET, UIController.PATH, new UIController());
        router.add(RequestMethod.GET, ResourcesController.PATH, new ResourcesController());

        router.add(RequestMethod.GET, SearchFieldsController.PATH, new SearchFieldsController());
        router.add(RequestMethod.GET, AllEntriesController.PATH, new AllEntriesController(requestStore));
        router.add(RequestMethod.GET, EntriesByTagController.PATH, new EntriesByTagController(requestStore));
        router.add(RequestMethod.GET, EntryController.PATH, new EntryController(requestStore));
    }

    @Override
    public String getName() {
        return "store-endpoints";
    }
}
