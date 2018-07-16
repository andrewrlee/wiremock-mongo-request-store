package uk.co.optimisticpanda.wmrs.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class PerStubConfigurationsTest {


    @Test
    public void aFewDifferentConfigs() {

        PerStubConfiguration config1 = PerStubConfiguration.builder()
                .withCollectionName("collection-1")
                .withPathExtractors(ImmutableMap.of(
                        "aaa", "111",
                        "bbb", "222"))
                .withBodyExtractors(ImmutableMap.of(
                        "aa1", "111",
                        "bb1", "222"))
                .withTags("tag-1")
                .build();

        PerStubConfiguration config2 = PerStubConfiguration.builder()
                .withCollectionName("collection-2")
                .withPathExtractors(ImmutableMap.of(
                        "aaa2", "111",
                        "bbb2", "222"))
                .withBodyExtractors(ImmutableMap.of(
                        "aa12", "111",
                        "bb12", "222"))
                .withTags("tag-2")
                .build();

        PerStubConfiguration config3 = PerStubConfiguration.builder()
                .withCollectionName("collection-2")
                .withPathExtractors(ImmutableMap.of(
                        "aaa3", "111",
                        "bbb3", "222"))
                .withBodyExtractors(ImmutableMap.of(
                        "aa13", "111",
                        "bb13", "222"))
                .withTags("tag-3")
                .build();

        PerStubConfigurations configurations = new PerStubConfigurations(asList(config1, config2, config3));

        assertThat(configurations.allSearchFields())
                .containsOnly(
                        "aa1", "bb1", "aaa", "bbb", "aa12", "aaa2", "bb13",
                        "bbb2", "bb12", "aa13", "aaa3", "bbb3");

        assertThat(configurations.searchFieldsForTag("tag-1"))
                .containsOnly("aa1", "aaa", "bbb", "bb1");

        assertThat(configurations.searchFieldsByStoreAndTag()).containsOnly(
                entry("collection-1",
                        ImmutableMap.of("tag-1", ImmutableSet.of("aa1", "bb1", "aaa", "bbb"))),
                entry("collection-2",
                        ImmutableMap.of(
                                "tag-2", ImmutableSet.of("aa12", "bb12", "aaa2", "bbb2"),
                                "tag-3", ImmutableSet.of("aa13", "bb13", "aaa3", "bbb3"))));
    }


    @Test
    public void sameConfigTwice() {

        PerStubConfiguration config1 = PerStubConfiguration.builder()
                .withCollectionName("collection-1")
                .withPathExtractors(ImmutableMap.of(
                        "aaa", "111",
                        "bbb", "222"))
                .withBodyExtractors(ImmutableMap.of(
                        "aa1", "111",
                        "bb1", "222"))
                .withTags("tag-1")
                .build();

        PerStubConfigurations configurations = new PerStubConfigurations(asList(config1, config1));

        assertThat(configurations.allSearchFields())
                .containsOnly("aa1", "aaa", "bbb", "bb1");

        assertThat(configurations.searchFieldsForTag("tag-1"))
                .containsOnly("aa1", "aaa", "bbb", "bb1");

        assertThat(configurations.searchFieldsByStoreAndTag()).containsOnly(
                entry("collection-1",
                        ImmutableMap.of("tag-1",
                                ImmutableSet.of("aa1", "bb1", "aaa", "bbb"))));
    }

}