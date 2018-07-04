package uk.co.optimisticpanda.wmrs.core;

import uk.co.optimisticpanda.wmrs.Entry;

public interface RequestStore {

    void save(String storeName, Entry entry);

}
