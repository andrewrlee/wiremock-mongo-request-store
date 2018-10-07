# Wiremock Mongo Request Store

By default wiremock stores requests in memory.

This extension stores requests in a mongo database and provides a web UI and a REST api to allow searching over those requests.

## Enabling the extensions:

Two extensions need to be enabled on a wiremock instance:

  * The request recorder post server action, to persist incoming requests
  * The HTTP endpoints, to enable the REST controllers and the Web UI.

e.g:

```java

    RequestStore store = new MongoRequestStore("mongodb://localhost:27017", "mock-server");

    WireMockServer server = new WireMockServer(options()

        .extensions(new RequestRecorder(store,
            BodyFieldExtractor.INSTANCE,
            UrlPathFieldExtractor.INSTANCE))

        .extensions(new StoreEndpoints(store))

        .fileSource(rootFileSource)

        .port(8080));

     server.start();
```

TODO: Look at simplifying extension registration

## Allow requests to be captured:

To allow a request to be captured, add the following post serve action to a mapping:

```json
  "postServeActions": {
    "mongo-request-recorder": {
      "collection-name": "requests"
    }
```

TODO: Add mechanism to default collection name based on a pattern

## Searching:

Extractors can be added to mappings to allow certain key values to be extracted from parts of the request.
Requests can then be queried using these key value pairs over the REST api and in the UI.
Values under the stored keys will automatically be indexed.

```json
  "postServeActions": {
    "mongo-request-recorder": {
      "collection-name": "requests",
      "fieldExtractors": {
        "path" : {
          "username" : "^/user/(.*?)/books/.*?$",
          "title" :    "^/user/.*?/books/(.*)$"
        },
        "body" : {
          "isbn": "$.isbn"
        }
      }
    }
  }

```

TODO:
  * Add mechanism to allow dynamically adding custom extractors - at the moment this is hardcoded into the extension object marshalling
  * Explain inbuilt custom extractors

## Tagging

Requests can be tagged with one or more tags which can then be used for filtering through the REST api and in the UI:

```json
  "postServeActions": {
    "mongo-request-recorder": {
      "collection-name": "requests",
      "tags": [
        "add-book"
      ]
    }
  }
```

## REST Api

The following rest apis can be accessed under: `/__admin/`:

 * /store
 * /store/fields
 * /store/{store-name}/entries/
 * /store/{store-name}/entries/tag/{tag}
 * /store/{store-name}/entries/{entry}
 * /store/static/{}/{}

TODO:
  * Expand on description
  * Add OpenApi/Raml specs
  * Sort out naming around store-name and collection-name

## Web interface

The web interface can be accessed at: `/__admin/store/`

TODO:
  * Add image

## Building

Dependencies:
  * Docker
  * Java 8
  * npm

to build and start the mock service run:

./make-and-run.sh

This will start a mongo service, build the web interface, and build and run the java application.

Once complete, the UI should be accessible under: `http://localhost:8080/__admin/store/`

TODO:
  * look at building with a single docker instance.

## Other

TODO:
  * Split out test project, rather than having server main in prod code.
  * Explain relationship between inbuilt api and this api - there isn't one
  * look at supporting a JDBC impl
  * look at supporting a different mongo versions
  * Release to maven repo.
  * CI
  * tests!
  * Move to kotlin/java10?

