package uk.co.optimisticpanda.wmrs.core.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.http.Request;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static uk.co.optimisticpanda.wmrs.core.admin.model.QueryParameters.limit;
import static uk.co.optimisticpanda.wmrs.core.admin.model.QueryParameters.offset;

public class Links {

    private Links() {
    }

    public static List<Link> forEntry(final String store, final String id) {
        return singletonList(detailsLink(store, id));
    }

    private static Link detailsLink(final String store, final String id) {
        return new Link("detail", format("/__admin/store/%s/entries/%s", store, id));
    }

    public static List<Link> forPage(final Request request) {
        String baseUrl = "/__admin" + request.getUrl().replaceAll("\\?.*$", "");
        ImmutableList.Builder<Link> links = ImmutableList.builder();

        return links
                .add(nextLink(baseUrl, offset(request).orElse(0), limit(request).orElse(25)))
                .add(previousLink(baseUrl, offset(request).orElse(0), limit(request).orElse(25)))
                .build();
    }

    private static Link previousLink(final String baseUrl,
                                     final Integer offset,
                                     final Integer limit) {
        int previousOffset = Math.max(offset - limit, 0);
        return new Link("previous", baseUrl + "?offset=" + previousOffset);
    }

    private static Link nextLink(final String baseUrl,
                                 final Integer offset,
                                 final Integer limit) {
        return new Link("next", baseUrl + "?offset=" + (offset + limit));
    }



    public static class Link {

        @JsonProperty("rel")
        private final String rel;
        @JsonProperty("href")
        private final String href;

        private Link(final String rel, final String href) {
            this.rel = rel;
            this.href = href;
        }

        public String getHref() {
            return href;
        }

        public String getRel() {
            return rel;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Link link = (Link) o;
            return Objects.equals(href, link.href)
                    && Objects.equals(rel, link.rel);
        }

        @Override
        public int hashCode() {
            return Objects.hash(href, rel);
        }

        @Override
        public String toString() {
            return toStringHelper(this)
                    .add("rel", rel)
                    .add("href", href)
                    .toString();
        }
    }
}
