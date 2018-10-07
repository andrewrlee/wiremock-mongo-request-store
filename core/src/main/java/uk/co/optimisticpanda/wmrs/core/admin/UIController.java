package uk.co.optimisticpanda.wmrs.core.admin;

import com.github.tomakehurst.wiremock.admin.AdminTask;
import com.github.tomakehurst.wiremock.admin.model.PathParams;
import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.google.common.io.Resources;
import com.google.common.net.MediaType;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.responseDefinition;
import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;

public class UIController implements AdminTask {
    static final String PATH = "/store";

    @Override
    public ResponseDefinition execute(Admin admin, Request request, PathParams pathParams) {
        try {
            byte[] content = toByteArray(Resources.getResource("public/index.html").openStream());
            return responseDefinition()
                    .withStatus(200)
                    .withBody(content)
                    .withHeader(CONTENT_TYPE, MediaType.HTML_UTF_8.toString())
                    .build();
        } catch (IOException e) {
            return responseDefinition().withStatus(500).build();
        }
    }
}
