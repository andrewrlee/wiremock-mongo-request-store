package uk.co.optimisticpanda.wmrs.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Entry {

    static final String TAGS = "tags";
    static final String FIELDS = "fields";
    static final String TIMESTAMP = "timestamp";

    @JsonSerialize(using= JsonDates.Serializer.class)
    @JsonDeserialize(using = JsonDates.Deserializer.class)
    @JsonProperty(TIMESTAMP)
    private LocalDateTime timestamp;

    @JsonDeserialize(as = LoggedRequest.class)
    @JsonProperty("request")
    private Request request;

    @JsonProperty("response")
    private ResponseDefinition response;

    @JsonProperty(TAGS)
    private List<String> tags;

    @JsonProperty(FIELDS)
    private Map<String, Object> fields;

    private Entry() {
    }

    public Entry(LocalDateTime timestamp,
                 Request request,
                 ResponseDefinition response,
                 List<String> tags,
                 Map<String, Object> fields) {
        this.timestamp = timestamp;
        this.request = request;
        this.response = response;
        this.tags = tags;
        this.fields =  fields;
    }
}
