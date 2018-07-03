package uk.co.optimisticpanda.wmrs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

import java.util.List;
import java.util.Map;

public class Entry {

    @JsonProperty("request")
    private final Request request;

    @JsonProperty("response")
    private final ResponseDefinition response;

    @JsonProperty("tags")
    private final List<String> tags;

    @JsonProperty("fields")
    private final Map<String, Object> fields;

    public Entry(Request request, ResponseDefinition response,
                 List<String> tags,
                 Map<String, Object> fields) {
        this.request = request;
        this.response = response;
        this.tags = tags;
        this.fields =  fields;
    }
}
