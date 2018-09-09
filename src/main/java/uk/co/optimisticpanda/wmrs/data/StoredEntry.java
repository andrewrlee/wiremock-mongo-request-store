package uk.co.optimisticpanda.wmrs.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import uk.co.optimisticpanda.wmrs.core.Entry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StoredEntry implements Entry {

    static final String TAGS = "tags";
    static final String FIELDS = "fields";
    static final String TIMESTAMP = "timestamp";

    @JsonProperty("id")
    private UUID id;

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

    private StoredEntry() {
    }

    public StoredEntry(final LocalDateTime timestamp,
                       final Request request,
                       final ResponseDefinition response,
                       final List<String> tags,
                       final Map<String, Object> fields) {
        this.id = UUID.randomUUID();
        this.timestamp = timestamp;
        this.request = request;
        this.response = response;
        this.tags = tags;
        this.fields =  fields;
    }

    @Override
    public String getId() {
        return id.toString();
    }
}
