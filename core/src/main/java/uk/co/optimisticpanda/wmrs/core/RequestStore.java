package uk.co.optimisticpanda.wmrs.core;

import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface RequestStore {

    void save(String storeName, Entry entry);

    List<? extends Entry> query(ListQuery query);

    Entry query(EntryQuery query);

    Entry newEntry(LocalDateTime now,
                   LoggedRequest request,
                   ResponseDefinition responseDefinition,
                   List<String> tags,
                   Map<String, Object> fields);
}
