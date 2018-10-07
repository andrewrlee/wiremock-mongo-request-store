package uk.co.optimisticpanda.wmrs.core;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public interface Entry {

    String getId();

    Set<String> getTags();

    LocalDateTime getTimestamp();

    Map<String, Object> getFields();
}
