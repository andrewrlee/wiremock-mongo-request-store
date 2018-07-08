package uk.co.optimisticpanda.wmrs.core;

import uk.co.optimisticpanda.wmrs.data.Entry;

import java.util.List;

public interface RequestStore {

    void save(String storeName, Entry entry);

    List<Entry> query(RequestQuery query);

}
