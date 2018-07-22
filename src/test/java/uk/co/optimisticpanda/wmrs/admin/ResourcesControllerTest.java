package uk.co.optimisticpanda.wmrs.admin;

import org.assertj.core.api.AbstractCharSequenceAssert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourcesControllerTest {

    @Test
    public void checkExtract() {

        extractedResourceFor("store/resources/").isEqualTo("assets/");
        extractedResourceFor("__admin/store/resources/").isEqualTo("assets/");
        extractedResourceFor("__admin/store/resources/").isEqualTo("assets/");
        extractedResourceFor("__admin/store/resources/main.css").isEqualTo("assets/main.css");
        extractedResourceFor("__admin/store/resources/js/main.js").isEqualTo("assets/js/main.js");
    }

    private AbstractCharSequenceAssert<?, String> extractedResourceFor(String path) {
        return assertThat(ResourcesController.extractedResource(path));
    }
}