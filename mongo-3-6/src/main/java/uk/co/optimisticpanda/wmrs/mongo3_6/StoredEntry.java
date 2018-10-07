package uk.co.optimisticpanda.wmrs.mongo3_6;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import uk.co.optimisticpanda.wmrs.core.Entry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StoredEntry implements Entry {

    static final String ID = "id";
    static final String TAGS = "tags";
    static final String FIELDS = "fields";
    static final String TIMESTAMP = "timestamp";

    @JsonProperty(ID)
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

    public StoredEntry(final UUID id,
                       final LocalDateTime timestamp,
                       final Request request,
                       final ResponseDefinition response,
                       final Set<String> tags,
                       final Map<String, Object> fields) {
        this.id = id;
        this.timestamp = timestamp;
        this.request = request;
        this.response = response;
        this.tags = ImmutableList.copyOf(tags);
        this.fields =  fields;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public Map<String, Object> getFields() {
        return fields;
    }

    @Override
    public String getId() {
        return id.toString();
    }

    @Override
    public Set<String> getTags() {
        return ImmutableSet.copyOf(tags);
    }
}
