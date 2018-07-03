package uk.co.optimisticpanda.wmrs;

public interface RequestStore {

    void save(String storeName, Entry entry);

}
