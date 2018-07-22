package uk.co.optimisticpanda.wmrs.admin;

import com.github.tomakehurst.wiremock.admin.AdminTask;
import com.github.tomakehurst.wiremock.admin.model.PathParams;
import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.common.net.MediaType;

import java.io.IOException;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.responseDefinition;
import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.MediaType.ANY_TEXT_TYPE;

public class ResourcesController implements AdminTask {
    static final String PATH = "/store/resources/{}/{}";

    private static final Map<String, MediaType> MEDIA_TYPE_MAP = ImmutableMap.<String, MediaType>builder()
            .put("css", MediaType.CSS_UTF_8)
            .put("json", MediaType.JSON_UTF_8)
            .put("js", MediaType.JAVASCRIPT_UTF_8)
            .put("png", MediaType.PNG)
            .put("gif", MediaType.JPEG)
            .build();

    @Override
    public ResponseDefinition execute(final Admin admin, final Request request, final PathParams pathParams) {

        String extension = Files.getFileExtension(request.getUrl());
        String resource = extractedResource(request.getUrl());

        try {
            byte[] content = toByteArray(Resources.getResource(resource).openStream());
            return responseDefinition()
                    .withStatus(200)
                    .withBody(content)
                    .withHeader(CONTENT_TYPE, MEDIA_TYPE_MAP.getOrDefault(extension, ANY_TEXT_TYPE).toString())
                    .build();
        } catch (IOException e) {
            return responseDefinition().withStatus(500).build();
        }
    }


    public static String extractedResource(final String path) {
        return "assets/" + path.replaceFirst(".*?store/resources/", "");
    }
}