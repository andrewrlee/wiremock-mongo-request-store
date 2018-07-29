package uk.co.optimisticpanda.wmrs.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.http.Request;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static uk.co.optimisticpanda.wmrs.admin.model.QueryParameters.limit;
import static uk.co.optimisticpanda.wmrs.admin.model.QueryParameters.offset;

public class Links {


    public static List<Link> create(final Request request) {
        System.out.println("request url: " + request.getUrl());
        System.out.println("request absolute url: " + request.getAbsoluteUrl());
        String baseUrl = "/__admin" + request.getUrl().replaceAll("\\?.*$", "");
        System.out.println("replaced: " + baseUrl);
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
