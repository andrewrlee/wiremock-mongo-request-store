package uk.co.optimisticpanda.wmrs.core;

import java.util.List;

public interface RequestStore {

    void save(String storeName, Entry entry);

    List<? extends Entry> query(ListQuery query);

    Entry query(EntryQuery query);
}
